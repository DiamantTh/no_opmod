<script>
  import { flip }     from 'svelte/animate'
  import { fade }     from 'svelte/transition'
  import { cubicOut } from 'svelte/easing'
  import Icon from '@iconify/svelte'
  import Navbar       from '../../components/Navbar.svelte'
  import { fmtItem, fmt, spreadClass } from '../../lib/utils.js'

  // ── State ──────────────────────────────────────────────────────────────────
  let items     = $state([])
  let loading   = $state(false)
  let error     = $state(null)
  let search    = $state('')
  let sortKey   = $state('item')
  let sortDir   = $state('asc')
  let lastFetch = $state(null)

  // Preise der vorherigen Ladung – für Flash-Erkennung
  let prevPrices = {}
  let flashKeys  = $state(new Set())

  // ── Derived ────────────────────────────────────────────────────────────────
  const statusBadgeClass = $derived(
    loading            ? 'badge-secondary' :
    error              ? 'badge-stale'     :
    items.length === 0 ? 'badge-empty'     : 'badge-fresh'
  )

  const statusText = $derived(
    loading          ? 'Laden…'             :
    error            ? 'Fehler'             :
    items.length === 0 ? 'Leer'             :
    items.length + ' Items'
  )

  const filteredItems = $derived.by(() => {
    let list = items
    const q  = search.toLowerCase().trim()
    if (q) list = list.filter(i => i.itemKey.toLowerCase().includes(q))
    const dir = sortDir === 'asc' ? 1 : -1
    return [...list].sort((a, b) => {
      if (sortKey === 'item') return dir * a.itemKey.localeCompare(b.itemKey)
      if (sortKey === 'buy')  return dir * (a.buy  - b.buy)
      if (sortKey === 'sell') return dir * (a.sell - b.sell)
      return 0
    })
  })

  // ── Sort-Helpers ────────────────────────────────────────────────────────────
  function setSort(key) {
    if (sortKey === key) sortDir = sortDir === 'asc' ? 'desc' : 'asc'
    else { sortKey = key; sortDir = 'asc' }
  }
  function sortCls(key) {
    if (sortKey !== key) return ''
    return sortDir === 'asc' ? 'sort-asc' : 'sort-desc'
  }

  // ── Daten laden ─────────────────────────────────────────────────────────────
  async function loadData() {
    loading = true
    error   = null
    try {
      const res = await fetch('/api/market')
      if (!res.ok) throw new Error('HTTP ' + res.status)
      const data     = await res.json()
      const newItems = Array.isArray(data) ? data : Object.values(data)

      // Flash-Erkennung: Zeilen deren Preis sich geändert hat kurz aufleuchten
      const changed = new Set()
      for (const item of newItems) {
        const prev = prevPrices[item.itemKey]
        if (prev !== undefined && (prev.buy !== item.buy || prev.sell !== item.sell)) {
          changed.add(item.itemKey)
        }
      }
      if (changed.size > 0) {
        flashKeys = changed
        setTimeout(() => { flashKeys = new Set() }, 1000)
      }

      // Preise für nächsten Vergleich speichern
      const pp = {}
      for (const it of newItems) pp[it.itemKey] = { buy: it.buy, sell: it.sell }
      prevPrices = pp

      items     = newItems
      lastFetch = new Date().toLocaleTimeString('de-DE')
    } catch (e) {
      error = 'Fehler beim Laden: ' + e.message
    } finally {
      loading = false
    }
  }

  // Einmalig beim Mount laden
  $effect.root(() => { loadData() })
</script>

<Navbar activePage="market" />

<div class="w-full px-4 py-3">

  <!-- ── Kopfzeile ────────────────────────────────────────────────────────── -->
  <div class="flex items-center gap-3 mb-3 flex-wrap">
    <h5 class="m-0 flex items-center gap-2 font-semibold text-base">
      <Icon icon="lucide:table" width={15} style="color:var(--vi-accent)" />Marktpreise
    </h5>
    <span class={statusBadgeClass}>{statusText}</span>
    <div class="ml-auto flex gap-2">
      <input
        type="text"
        class="search-input"
        style="width:220px"
        placeholder="Suchen…"
        bind:value={search}
      >
      <button class="btn-icon" onclick={loadData} title="Aktualisieren">
        <Icon icon="lucide:refresh-cw" width={14} class={loading ? 'spin' : ''} />
      </button>
    </div>
  </div>

  <!-- ── Lade-Spinner ──────────────────────────────────────────────────────── -->
  {#if loading && items.length === 0}
    <div class="loading-overlay" transition:fade={{ duration: 150 }}>
      <div class="text-center">
        <span class="inline-block w-6 h-6 border-2 rounded-full animate-spin mb-2 mx-auto block"
              style="border-color:var(--vi-accent); border-top-color:transparent"></span>
        <div>Lade Marktpreise…</div>
      </div>
    </div>
  {/if}

  <!-- ── Fehler ────────────────────────────────────────────────────────────── -->
  {#if error && !loading}
    <div class="rounded p-3 mb-3 text-sm"
         style="background:#450a0a; border:1px solid #7f1d1d; color:#fca5a5"
         transition:fade>{error}</div>
  {/if}

  <!-- ── Tabelle ───────────────────────────────────────────────────────────── -->
  {#if !loading || items.length > 0}
    <div class="vi-card" transition:fade={{ duration: 200 }}>
      <div class="overflow-x-auto">
        <table class="vi-table">
          <thead>
            <tr>
              <th onclick={() => setSort('item')} class={sortCls('item')}>Item</th>
              <th onclick={() => setSort('buy')}  class="text-right {sortCls('buy')}">Kaufpreis</th>
              <th onclick={() => setSort('sell')} class="text-right {sortCls('sell')}">Verkaufspreis</th>
              <th class="text-right">Spanne</th>
            </tr>
          </thead>
          <tbody>
            {#each filteredItems as item (item.itemKey)}
              <tr
                animate:flip={{ duration: 280, easing: cubicOut }}
                class:row-flash={flashKeys.has(item.itemKey)}
              >
                <td>
                  <div class="flex items-center gap-2">
                    <img
                      src="/api/icon/{item.itemKey}"
                      class="item-icon" alt=""
                      onerror={(e) => e.currentTarget.style.display = 'none'}
                    >
                    <a href="/history?m={item.itemKey}"
                       class="font-medium no-underline transition-colors"
                       style="color:var(--vi-text)">
                      {fmtItem(item.itemKey)}
                    </a>
                  </div>
                </td>
                <td class="text-right">
                  {#if item.buy > 0}
                    <span class="price-buy">{fmt(item.buy)}</span>
                  {:else}
                    <span class="price-na">–</span>
                  {/if}
                </td>
                <td class="text-right">
                  {#if item.sell > 0}
                    <span class="price-sell">{fmt(item.sell)}</span>
                  {:else}
                    <span class="price-na">–</span>
                  {/if}
                </td>
                <td class="text-right">
                  {#if item.buy > 0 && item.sell > 0}
                    <span class="{spreadClass(item.buy, item.sell)} tabular">
                      {fmt(item.buy - item.sell)}
                    </span>
                  {:else}
                    <span class="price-na">–</span>
                  {/if}
                </td>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
      <div class="vi-card-footer flex justify-between">
        <span>{filteredItems.length} / {items.length} Einträge</span>
        {#if lastFetch}<span>Stand: {lastFetch}</span>{/if}
      </div>
    </div>
  {/if}

</div>

<style>
  .tabular { font-variant-numeric: tabular-nums; }
</style>
