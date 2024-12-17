
# Uncomment this if you're using STL in your project
# See CPLUSPLUS-SUPPORT.html in the NDK documentation for more information
#APP_STL := stlport_static

XASH_64BIT ?= 0
XASH_SDL ?= 0
XASH_VGUI ?= 0
# If non-zero, works only if single ABI selected
XASH_THREAD_NUM ?= 0

# Google fun for old android versions
APP_PLATFORM := android-17

#NDK_TOOLCHAIN_VERSION := clang

CFLAGS_OPT :=  -O3 -fomit-frame-pointer -ggdb -funsafe-math-optimizations -ftree-vectorize -fgraphite-identity -floop-interchange -funsafe-loop-optimizations -finline-limit=256 -pipe
CFLAGS_OPT_ARM := -mthumb -mfpu=neon -mcpu=cortex-a53 -DVECTORIZE_SINCOS -fPIC -DHAVE_EFFICIENT_UNALIGNED_ACCESS
CFLAGS_OPT_ARM64 := -mcpu=cortex-a53 -DVECTORIZE_SINCOS
CFLAGS_OPT_X86_64 := -funroll-loops
CFLAGS_OPT_X86 := -mtune=atom -march=atom -mssse3 -mfpmath=sse -funroll-loops -DVECTORIZE_SINCOS -DHAVE_EFFICIENT_UNALIGNED_ACCESS
CFLAGS_HARDFP := -D_NDK_MATH_NO_SOFTFP=1 -mhard-float -mfloat-abi=hard -DLOAD_HARDFP -DSOFTFP_LINK
APPLICATIONMK_PATH = $(call my-dir)

ifneq ($(NDK_TOOLCHAIN_VERSION),clang)
APP_CFLAGS += -Wl,--no-undefined
CFLAGS_OPT_ARM += -mvectorize-with-neon-quad
else
APP_CFLAGS += -D__ANDROID__
CFLAGS_OPT_ARM += --target=armv7a-linux-androideabi15
CFLAGS_OPT_ARM64 += --target=aarch64-linux-android
CFLAGS_OPT_X86 += --target=i686-linux-android
CFLAGS_OPT_X86_64 += --target=x86_64-linux-android
endif

NANOGL_PATH := $(APPLICATIONMK_PATH)/src/NanoGL/nanogl

XASH3D_PATH := $(APPLICATIONMK_PATH)/src/Xash3D/xash3d

HLSDK_PATH  := $(APPLICATIONMK_PATH)/src/HLSDK/halflife/

XASH3D_CONFIG := $(APPLICATIONMK_PATH)/xash3d_config.mk

ifeq ($(XASH_64BIT),1)
#APP_ABI := x86_64 arm64-v8a
APP_ABI := arm64-v8a
else
APP_ABI := x86 armeabi-v7a-hard
endif
# Use armeabi-v7a-hard to enable hardfloat (r12b and higher ndks dosent support hard float)
# Mods are built with both ABI support
# Build both armeabi-v7a-hard and armeabi-v7a supported only for mods, not for engine

APP_MODULES := xash menu client server NanoGL
ifeq ($(XASH_SDL),1)
	APP_MODULES += SDL2
endif

ifeq ($(XASH_VGUI),1)
	APP_MODULES += vgui_support
endif
