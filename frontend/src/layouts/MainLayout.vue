<template>
  <div class="layout-container">
    <NavBar :transparent="transparentHeader" />
    <main class="main-content">
      <slot></slot>
    </main>
    <Footer v-if="showFooter" />
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue'
import Footer from '@/components/Footer.vue'

defineProps({
  transparentHeader: {
    type: Boolean,
    default: false
  },
  showFooter: {
    type: Boolean,
    default: true
  }
})
</script>

<style scoped>
.layout-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
}
</style>
