/**
 * 批量选择 Composable
 * 提供全选/单选/批量删除通用逻辑
 */
import { ref, computed } from 'vue'

export function useBatchSelect<T extends { id: string }>(
  getFilteredList: () => T[],
) {
  const selectedIds = ref<Set<string>>(new Set())

  const allSelected = computed(() => {
    const list = getFilteredList()
    return list.length > 0 && list.every(item => selectedIds.value.has(item.id))
  })

  function toggleSelectAll() {
    const list = getFilteredList()
    if (allSelected.value) {
      selectedIds.value = new Set()
    } else {
      selectedIds.value = new Set(list.map(item => item.id))
    }
  }

  function toggleSelect(id: string) {
    const next = new Set(selectedIds.value)
    if (next.has(id)) next.delete(id)
    else next.add(id)
    selectedIds.value = next
  }

  function clearSelection() {
    selectedIds.value = new Set()
  }

  return { selectedIds, allSelected, toggleSelectAll, toggleSelect, clearSelection }
}
