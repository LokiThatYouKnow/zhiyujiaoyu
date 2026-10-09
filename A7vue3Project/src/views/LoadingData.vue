<template>
    <div class="loading-container">
        <!-- 标题 -->
        <h1 class="loading-title">
            <span>数据</span>
            <span>装载</span>
            <span>中</span>
        </h1>

        <!-- 主要加载动画 -->
        <div class="loader">
            <!-- 中心球体 -->
            <div class="loader-center"></div>

            <!-- 轨道粒子 -->
            <div class="orbit-particle"></div>
            <div class="orbit-particle"></div>
            <div class="orbit-particle"></div>
            <div class="orbit-particle"></div>

            <!-- 波形指示器 -->
            <div class="wave-indicator">
                <div class="wave-bar"></div>
                <div class="wave-bar"></div>
                <div class="wave-bar"></div>
                <div class="wave-bar"></div>
                <div class="wave-bar"></div>
            </div>
        </div>

        <!-- 动态文字 -->
        <div class="typing-container">
            <p class="typing-text">
                <i class="fa fa-circle-o-notch fa-spin typing-icon"></i>
                正在装载数据
            </p>
        </div>

        <!-- 进度指示器 -->
        <div class="progress-bar">
            <div class="progress-fill"></div>
        </div>

        <!-- 版权信息 -->
        <p class="copyright">请稍候...</p>
    </div>
</template>

<script lang="ts" setup>
</script>

<style scoped>
/* 主容器样式 */
.loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 100vh;
    padding: 20px;
    font-family: Arial, sans-serif;
    background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%);
}

/* 标题样式 */
.loading-title {
    margin-bottom: 40px;
    font-size: clamp(1.5rem, 3vw, 2.5rem);
    font-weight: bold;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.loading-title span:nth-child(1) {
    color: #4285f4;
}

.loading-title span:nth-child(2) {
    color: #34a853;
}

.loading-title span:nth-child(3) {
    color: #fbbc05;
}

/* 主要加载动画 */
.loader {
    position: relative;
    width: 160px;
    height: 160px;
    margin-bottom: 60px;
}

/* 中心球体 */
.loader-center {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 64px;
    height: 64px;
    border-radius: 50%;
    background: linear-gradient(135deg, #4285f4 0%, #34a853 100%);
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
    animation: pulse-slow 3s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

/* 轨道粒子 */
.orbit-particle {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    transform-origin: center;
    animation: orbit 6s linear infinite;
}

.orbit-particle:nth-child(1) {
    background: #fbbc05;
    animation-delay: 0s;
}

.orbit-particle:nth-child(2) {
    background: #ea4335;
    animation-delay: -1.5s;
    width: 24px;
    height: 24px;
}

.orbit-particle:nth-child(3) {
    background: #4285f4;
    animation-delay: -3s;
    width: 16px;
    height: 16px;
}

.orbit-particle:nth-child(4) {
    background: #34a853;
    animation-delay: -4.5s;
    width: 12px;
    height: 12px;
}

/* 波形指示器 */
.wave-indicator {
    position: absolute;
    bottom: -20px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    gap: 4px;
}

.wave-bar {
    width: 8px;
    height: 32px;
    border-radius: 4px;
    animation: wave 1.5s ease-in-out infinite;
}

.wave-bar:nth-child(1) {
    background: rgba(66, 133, 244, 0.7);
    animation-delay: 0s;
}

.wave-bar:nth-child(2) {
    background: rgba(52, 168, 83, 0.7);
    animation-delay: 0.1s;
}

.wave-bar:nth-child(3) {
    background: rgba(251, 188, 5, 0.7);
    animation-delay: 0.2s;
}

.wave-bar:nth-child(4) {
    background: rgba(234, 67, 53, 0.7);
    animation-delay: 0.3s;
}

.wave-bar:nth-child(5) {
    background: rgba(66, 133, 244, 0.7);
    animation-delay: 0.4s;
}

/* 动态文字 */
.typing-container {
    position: relative;
    width: 256px;
    margin-top: 20px;
    overflow: hidden;
}

.typing-text {
    display: inline-block;
    font-size: 16px;
    font-weight: 500;
    color: #202124;
    letter-spacing: 1px;
    white-space: nowrap;
    border-right: 2px solid #666;
    animation: typing 3.5s steps(40, end), blink .75s step-end infinite;
}

.typing-icon {
    margin-right: 8px;
    animation: pulse 1.5s infinite;
}

/* 进度指示器 */
.progress-bar {
    width: 256px;
    height: 8px;
    margin-top: 24px;
    border-radius: 4px;
    background-color: #e0e0e0;
    overflow: hidden;
}

.progress-fill {
    width: 75%;
    height: 100%;
    border-radius: 4px;
    background: linear-gradient(90deg, #4285f4 0%, #34a853 100%);
    animation: progress 2s ease-in-out infinite;
}

/* 版权信息 */
.copyright {
    margin-top: 32px;
    font-size: 12px;
    color: #666;
}

/* 动画定义 */
@keyframes pulse-slow {

    0%,
    100% {
        transform: translate(-50%, -50%) scale(1);
        opacity: 0.8;
    }

    50% {
        transform: translate(-50%, -50%) scale(1.1);
        opacity: 1;
    }
}

@keyframes orbit {
    0% {
        transform: translate(-50%, -50%) rotate(0deg) translateX(120px) rotate(0deg);
    }

    100% {
        transform: translate(-50%, -50%) rotate(360deg) translateX(120px) rotate(-360deg);
    }
}

@keyframes wave {

    0%,
    100% {
        transform: scaleY(0.3);
    }

    50% {
        transform: scaleY(1);
    }
}

@keyframes typing {
    from {
        width: 0
    }

    to {
        width: 100%
    }
}

@keyframes blink {

    from,
    to {
        border-color: transparent
    }

    50% {
        border-color: #666
    }
}

@keyframes progress {
    0% {
        width: 10%
    }

    50% {
        width: 75%
    }

    100% {
        width: 10%
    }
}
</style>