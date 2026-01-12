<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-brand">
        <h2 class="brand-title">冀忆红途</h2>
        <p class="brand-desc">河北红色基因库数字平台<br>传承红色基因，赓续燕赵血脉</p>
      </div>
      
      <div class="footer-links-group">
        <div class="link-column">
          <h3>平台服务</h3>
          <router-link to="/experience">数字体验馆</router-link>
          <router-link to="/mall">文创商城</router-link>
          <router-link to="/tourism">智慧旅游</router-link>
        </div>
        
        <div class="link-column">
          <h3>关于我们</h3>
          <a href="#">项目介绍</a>
          <a href="#">团队成员</a>
          <a href="#">联系我们</a>
        </div>
        
        <div class="link-column">
          <h3>帮助支持</h3>
          <a href="#">使用指南</a>
          <a href="#">常见问题</a>
          <a href="#">意见反馈</a>
        </div>
      </div>
    </div>
    
    <div class="footer-bottom">
      <p>&copy; 2024 河北红色基因库数字平台 (Hebei Red Gene Database). All Rights Reserved.</p>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  background-color: var(--bg-dark);
  color: white;
  padding: 4rem 2rem 1rem;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 4rem;
  margin-bottom: 3rem;
}

.brand-title {
  color: white;
  font-size: 2rem;
  margin-bottom: 1rem;
}

.brand-desc {
  color: rgba(255,255,255,0.6);
  line-height: 1.6;
}

.footer-links-group {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2rem;
}

.link-column h3 {
  color: var(--color-accent);
  font-size: 1.1rem;
  margin-bottom: 1.5rem;
}

.link-column a {
  display: block;
  color: rgba(255,255,255,0.7);
  margin-bottom: 0.8rem;
  transition: color 0.2s;
}

.link-column a:hover {
  color: white;
}

.footer-bottom {
  border-top: 1px solid rgba(255,255,255,0.1);
  padding-top: 2rem;
  text-align: center;
  color: rgba(255,255,255,0.4);
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .footer-links-group {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
