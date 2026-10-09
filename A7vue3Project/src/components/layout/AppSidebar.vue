<template>
  <aside class="sidebar" :class="{ collapsed }" :style="sidebarStyle">
    <!-- Logo -->
    <div class="sidebar-brand">
      <div class="brand-icon" :style="{ background: themeGradient }">
        <svg viewBox="0 0 24 24" fill="none" width="22" height="22">
          <path d="M12 2L2 7L12 12L22 7L12 2Z" fill="white" fill-opacity="0.9"/>
          <path d="M2 17L12 22L22 17" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M2 12L12 17L22 12" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <transition name="fade">
        <div v-if="!collapsed" class="brand-text">
          <span class="brand-name">智语教学</span>
          <span class="brand-role">{{ roleLabel }}</span>
        </div>
      </transition>
    </div>

    <!-- Navigation -->
    <nav class="sidebar-nav">
      <template v-for="item in navItems" :key="item.key">
        <!-- Group -->
        <div v-if="item.children" class="nav-group">
          <button
            v-if="!collapsed"
            class="nav-group-label"
            @click="toggleGroup(item.key)"
          >
            {{ item.label }}
          </button>
          <div v-show="collapsed || openGroups.has(item.key)" class="nav-group-items">
            <button
              v-for="child in item.children"
              :key="child.key"
              class="nav-item"
              :class="{ active: activeTab === child.key }"
              :title="collapsed ? child.label : ''"
              @click="$emit('nav', child.key)"
            >
              <span class="nav-icon" v-html="child.icon"></span>
              <span v-if="!collapsed" class="nav-label">{{ child.label }}</span>
              <span v-if="!collapsed && activeTab === child.key" class="nav-dot"></span>
            </button>
          </div>
        </div>
        <!-- Single item -->
        <button
          v-else
          class="nav-item"
          :class="{ active: activeTab === item.key }"
          :title="collapsed ? item.label : ''"
          @click="$emit('nav', item.key)"
        >
          <span class="nav-icon" v-html="item.icon"></span>
          <span v-if="!collapsed" class="nav-label">{{ item.label }}</span>
          <span v-if="!collapsed && activeTab === item.key" class="nav-dot"></span>
        </button>
      </template>
    </nav>

    <!-- Collapse Toggle -->
    <button class="sidebar-toggle" @click="$emit('toggle')" :style="{ background: themeGradient }">
      <svg v-if="!collapsed" viewBox="0 0 24 24" fill="none" width="16" height="16">
        <path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <svg v-else viewBox="0 0 24 24" fill="none" width="16" height="16">
        <path d="M9 18l6-6-6-6" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>
  </aside>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue'

export interface NavItem {
  key: string
  label: string
  icon: string
  children?: NavItem[]
}

const props = defineProps<{
  role: 'teacher' | 'student' | 'admin'
  navItems: NavItem[]
  activeTab: string
  collapsed: boolean
}>()

defineEmits<{
  nav: [key: string]
  toggle: []
}>()

const openGroups = ref(new Set<string>())

function toggleGroup(key: string) {
  if (openGroups.value.has(key)) {
    openGroups.value.delete(key)
  } else {
    openGroups.value.add(key)
  }
}

const themes = {
  teacher: { gradient: 'linear-gradient(135deg, #F59E0B, #D97706)', label: '教师端', color: '#F59E0B' },
  student: { gradient: 'linear-gradient(135deg, #0D9488, #14B8A6)', label: '学生端', color: '#0D9488' },
  admin: { gradient: 'linear-gradient(135deg, #7C3AED, #8B5CF6)', label: '管理端', color: '#7C3AED' },
} as const

const theme = computed(() => themes[props.role])
const themeGradient = computed(() => theme.value.gradient)
const roleLabel = computed(() => theme.value.label)

const sidebarStyle = computed(() => ({
  '--sidebar-accent': theme.value.color,
  width: props.collapsed ? '72px' : '240px',
}))
</script>

<style scoped>
.sidebar {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: linear-gradient(180deg, #0F172A 0%, #1E293B 50%, #0F172A 100%);
  transition: width 0.3s ease;
  flex-shrink: 0;
  z-index: 100;
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255,255,255,0.06);
  transition: padding 0.3s;
}
.collapsed .sidebar-brand { padding: 20px 16px; justify-content: center; }

.brand-icon {
  width: 38px; height: 38px; min-width: 38px;
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.3);
}
.brand-text { display: flex; flex-direction: column; overflow: hidden; white-space: nowrap; }
.brand-name { font-size: 16px; font-weight: 700; color: #fff; line-height: 1.3; }
.brand-role { font-size: 11px; color: #94A3B8; font-weight: 500; }

/* Navigation */
.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  padding: 12px 10px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-group { margin-bottom: 4px; }
.nav-group-label {
  width: 100%;
  padding: 8px 14px 4px;
  border: none;
  background: none;
  color: #64748B;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1.2px;
  cursor: pointer;
  text-align: left;
  font-family: inherit;
}
.nav-group-label:hover { color: #94A3B8; }

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 14px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #CBD5E1;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
  text-align: left;
  position: relative;
}
.nav-item:hover { color: #fff; background: rgba(255,255,255,0.06); }
.nav-item.active {
  color: #fff;
  background: linear-gradient(135deg, rgba(99,102,241,0.2), rgba(139,92,246,0.12));
  font-weight: 600;
}

.nav-icon {
  display: flex; align-items: center; justify-content: center;
  width: 20px; height: 20px; min-width: 20px;
  opacity: 0.7;
}
.nav-item.active .nav-icon { opacity: 1; }
.nav-icon :deep(svg) { width: 18px; height: 18px; }

.nav-label { flex: 1; white-space: nowrap; overflow: hidden; }
.nav-dot {
  width: 6px; height: 6px;
  border-radius: 50%;
  background: var(--sidebar-accent, #4F46E5);
}

.collapsed .nav-item { justify-content: center; padding: 12px; }
.collapsed .nav-group-label { display: none; }

/* Toggle */
.sidebar-toggle {
  position: absolute;
  right: -14px; top: 50%;
  transform: translateY(-50%);
  width: 28px; height: 56px;
  border-radius: 0 14px 14px 0;
  border: none;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  color: #fff;
  box-shadow: 0 4px 14px rgba(0,0,0,0.3);
  transition: all 0.3s;
  z-index: 110;
}
.sidebar-toggle:hover { width: 34px; }

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
