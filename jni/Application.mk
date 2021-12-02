
# Uncomment this if you're using STL in your project
# See CPLUSPLUS-SUPPORT.html in the NDK documentation for more information
#APP_STL := stlport_static

XASH_64BIT ?= 0
XASH_SDL ?= 0
# If non-zero, works only if single ABI selected
XASH_THREAD_NUM ?= 0

#NDK_TOOLCHAIN_VERSION := clang

ifneq ($(NDK_TOOLCHAIN_VERSION),clang)
APP_CFLAGS += -Wl,--no-undefined
else
APP_CFLAGS +=
endif

CFLAGS_OPT :=  -O3 -fomit-frame-pointer -ggdb -funsafe-math-optimizations -ftree-vectorize -fgraphite-identity -floop-interchange -funsafe-loop-optimizations -finline-limit=256 -pipe
CFLAGS_OPT_ARM := -mthumb -mfpu=neon -mcpu=cortex-a9 -pipe -mvectorize-with-neon-quad -DVECTORIZE_SINCOS -fPIC -DHAVE_EFFICIENT_UNALIGNED_ACCESS
CFLAGS_OPT_ARM64 := -pipe
CFLAGS_OPT_X86_64 := -pipe -funroll-loops
CFLAGS_OPT_X86 := -mtune=atom -march=atom -mssse3 -mfpmath=sse -funroll-loops -pipe -DVECTORIZE_SINCOS -DHAVE_EFFICIENT_UNALIGNED_ACCESS
APPLICATIONMK_PATH = $(call my-dir)

NANOGL_PATH := $(APPLICATIONMK_PATH)/src/NanoGL/nanogl

XASH3D_PATH := $(APPLICATIONMK_PATH)/src/Xash3D/xash3d

HLSDK_PATH  := $(APPLICATIONMK_PATH)/src/HLSDK/halflife/

XASH3D_CONFIG := $(APPLICATIONMK_PATH)/xash3d_config.mk

ifeq ($(XASH_64BIT),1)
APP_ABI := x86_64 arm64-v8a
else
APP_ABI := x86 armeabi-v7a
endif
# Use armeabi-v7a-hard to enable hardfloat (r12b and higher ndks dosent support hard float)
# Mods are built with both ABI support
# Build both armeabi-v7a-hard and armeabi-v7a supported only for mods, not for engine

APP_MODULES := xash menu client server NanoGL
ifeq ($(XASH_SDL),1)
	APP_MODULES += SDL2
endif

