// proreg.cpp - Hook FFMax 2.126.1
#include <jni.h>
#include <android/log.h>
#include <dlfcn.h>
#include <cstdint>
#include <atomic>
#include "dobby.h"

#define LOG(...) __android_log_print(ANDROID_LOG_INFO, "proreg", __VA_ARGS__)

static uintptr_t g_base = 0;
static std::atomic<bool>  g_aim{false};
static std::atomic<float> g_hs_pct{0.90f};

static constexpr uintptr_t OFF_PlayerReaper_TakeDamage = 0x8f20ac0;
static constexpr uintptr_t OFF_GetAttackableCenterWS   = 0x9156684;
static constexpr uintptr_t OFF_GetBodyPartIndex        = 0x9ad4108;

static inline void* RVA(uintptr_t rva) {
    return (void*)(g_base + rva);
}

typedef void (*TakeDamage_t)(void* self, float dmg, void* attacker);
static TakeDamage_t orig_PlayerReaper_TakeDamage = nullptr;

static void hook_PlayerReaper_TakeDamage(void* self, float dmg, void* attacker) {
    if (g_aim.load()) {
        float mult = 1.0f + (g_hs_pct.load() - 0.5f) * 2.0f;
        if (mult > 1.0f) dmg *= mult;
        LOG("TakeDamage dmg=%.2f", dmg);
    }
    orig_PlayerReaper_TakeDamage(self, dmg, attacker);
}

typedef void (*GetCenterWS_t)(void* self, float* out);
static GetCenterWS_t orig_GetCenterWS = nullptr;

static void hook_GetCenterWS(void* self, float* out) {
    orig_GetCenterWS(self, out);
    if (out && g_aim.load()) {
        LOG("CenterWS (%.2f,%.2f,%.2f)", out[0], out[1], out[2]);
    }
}

typedef int (*GetBodyPartIndex_t)(void* self);
static GetBodyPartIndex_t orig_GetBodyPartIndex = nullptr;

static int hook_GetBodyPartIndex(void* self) {
    int r = orig_GetBodyPartIndex(self);
    LOG("BodyPartIndex=%d", r);
    return r;
}

extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM*, void*) {
    void* h = dlopen("libil2cpp.so", RTLD_NOW);
    if (!h) return JNI_VERSION_1_6;
    Dl_info di{};
    if (dladdr(h, &di)) g_base = (uintptr_t)di.dli_fbase;
    LOG("base=%p", (void*)g_base);
    if (g_base) {
        DobbyHook(RVA(OFF_PlayerReaper_TakeDamage),
                  (void*)hook_PlayerReaper_TakeDamage,
                  (void**)&orig_PlayerReaper_TakeDamage);
        DobbyHook(RVA(OFF_GetAttackableCenterWS),
                  (void*)hook_GetCenterWS,
                  (void**)&orig_GetCenterWS);
        DobbyHook(RVA(OFF_GetBodyPartIndex),
                  (void*)hook_GetBodyPartIndex,
                  (void**)&orig_GetBodyPartIndex);
    }
    return JNI_VERSION_1_6;
}

extern "C" JNIEXPORT void JNICALL
Java_com_proreg_NativeBridge_setAim(JNIEnv*, jclass, jboolean on) {
    g_aim.store(on);
}

extern "C" JNIEXPORT void JNICALL
Java_com_proreg_NativeBridge_setHeadshot(JNIEnv*, jclass, jint pct) {
    g_hs_pct.store(pct / 100.0f);
}

extern "C" JNIEXPORT void JNICALL
Java_com_proreg_NativeBridge_init(JNIEnv*, jclass) {}

extern "C" JNIEXPORT void JNICALL
Java_com_proreg_NativeBridge_setEsp(JNIEnv*, jclass, jboolean) {}

extern "C" JNIEXPORT void JNICALL
Java_com_proreg_NativeBridge_setVpn(JNIEnv*, jclass, jboolean) {}

extern "C" JNIEXPORT void JNICALL
Java_com_proreg_NativeBridge_setAntiban(JNIEnv*, jclass, jboolean) {}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_proreg_NativeBridge_getEspRects(JNIEnv* env, jclass) {
    return env->NewFloatArray(0);
}
