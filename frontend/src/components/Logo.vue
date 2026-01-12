<template>
  <div
    class="logo-container"
    :class="{ small: size === 'small', large: size === 'large' }"
  >
    <svg
      class="logo-svg"
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 100 100"
      :width="width"
      :height="height"
    >
      <defs>
        <!-- 红色渐变 -->
        <linearGradient
          id="redGradient"
          x1="0%"
          y1="0%"
          x2="100%"
          y2="100%"
        >
          <stop
            offset="0%"
            style="stop-color: #ff4757; stop-opacity: 1"
          />
          <stop
            offset="100%"
            style="stop-color: #c41e3a; stop-opacity: 1"
          />
        </linearGradient>
        <!-- 金色渐变 -->
        <linearGradient
          id="goldGradient"
          x1="0%"
          y1="0%"
          x2="100%"
          y2="100%"
        >
          <stop
            offset="0%"
            style="stop-color: #ffd700; stop-opacity: 1"
          />
          <stop
            offset="100%"
            style="stop-color: #ffb700; stop-opacity: 1"
          />
        </linearGradient>
        <!-- 光晕效果 -->
        <radialGradient id="glowGradient">
          <stop
            offset="0%"
            style="stop-color: #fff; stop-opacity: 0.3"
          />
          <stop
            offset="100%"
            style="stop-color: #fff; stop-opacity: 0"
          />
        </radialGradient>
      </defs>

      <!-- 背景圆形 -->
      <circle
        cx="50"
        cy="50"
        r="48"
        fill="url(#redGradient)"
      />

      <!-- 内圈光晕 -->
      <circle
        cx="50"
        cy="50"
        r="42"
        fill="url(#glowGradient)"
      />

      <!-- 主体设计：山峰+路径 -->
      <g transform="translate(50, 50)">
        <!-- 左侧山峰 -->
        <path
          d="M -25,10 L -15,-15 L -5,10 Z"
          fill="url(#goldGradient)"
          opacity="0.9"
        />
        
        <!-- 中间山峰（最高） -->
        <path
          d="M -10,10 L 0,-20 L 10,10 Z"
          fill="url(#goldGradient)"
        />
        
        <!-- 右侧山峰 -->
        <path
          d="M 5,10 L 15,-15 L 25,10 Z"
          fill="url(#goldGradient)"
          opacity="0.9"
        />

        <!-- 红色道路/足迹 -->
        <path
          d="M -30,15 Q -15,12 0,15 T 30,15"
          stroke="#fff"
          stroke-width="2.5"
          fill="none"
          stroke-linecap="round"
        />

        <!-- 五角星（顶部） -->
        <g transform="translate(0, -25)">
          <path
            d="M 0,-5 L 1.5,-1.5 L 5,0 L 1.5,1.5 L 0,5 L -1.5,1.5 L -5,0 L -1.5,-1.5 Z"
            fill="#ffd700"
          />
        </g>

        <!-- 底部文字装饰线 -->
        <line
          x1="-20"
          y1="20"
          x2="20"
          y2="20"
          stroke="#ffd700"
          stroke-width="2"
          opacity="0.6"
        />
      </g>

      <!-- 外圈装饰 -->
      <circle
        cx="50"
        cy="50"
        r="46"
        fill="none"
        stroke="#ffd700"
        stroke-width="0.5"
        opacity="0.5"
      />
    </svg>
  </div>
</template>

<script setup lang="ts">
interface Props {
  size?: 'small' | 'medium' | 'large'
  width?: string
  height?: string
}

const props = withDefaults(defineProps<Props>(), {
  size: 'medium',
  width: '40px',
  height: '40px'
})
</script>

<style scoped>
.logo-container {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.logo-svg {
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
  transition: transform 0.3s ease;
}

.logo-container:hover .logo-svg {
  transform: scale(1.05);
}

.logo-container.small .logo-svg {
  width: 32px;
  height: 32px;
}

.logo-container.large .logo-svg {
  width: 60px;
  height: 60px;
}
</style>
