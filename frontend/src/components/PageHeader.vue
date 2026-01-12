<template>
  <div class="page-header">
    <div class="header-content">
      <div class="header-left">
        <span v-if="icon" class="header-icon">{{ icon }}</span>
        <div class="header-text">
          <h1 class="header-title">{{ title }}</h1>
          <p v-if="subtitle" class="header-subtitle">{{ subtitle }}</p>
        </div>
      </div>
      <div class="header-actions">
        <slot name="actions"></slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  icon?: string
  title: string
  subtitle?: string
}>()
</script>

<style scoped>
.page-header {
  background: linear-gradient(135deg, rgba(139, 30, 63, 0.95), rgba(196, 30, 58, 0.95));
  backdrop-filter: blur(10px);
  border-bottom: 2px solid rgba(212, 149, 108, 0.3);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  animation: slideDown 0.5s ease-out;
}

@keyframes slideDown {
  from {
    transform: translateY(-100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 1.5rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.header-icon {
  font-size: 3rem;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

.header-text {
  color: white;
}

.header-title {
  font-size: 1.8rem;
  font-weight: 800;
  color: #ffd700;
  margin: 0;
  letter-spacing: 2px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.header-subtitle {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  letter-spacing: 1px;
}

.header-actions {
  display: flex;
  gap: 1rem;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }

  .header-left {
    flex-direction: column;
  }
}
</style>
