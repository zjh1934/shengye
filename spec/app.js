/* 纯机械渲染层：不含任何诊断或设计判断，只把形态图与文档显示出来 */
const esc = (s) => String(s == null ? '' : s).replace(/[&<>"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));
const state = {group:'ALL', q:'', view:'figures', theme:'light', mode:'grid'};

function switchView(v){
  state.view = v;
  document.querySelectorAll('.view').forEach(e => e.classList.remove('on'));
  const el = document.getElementById('v-' + v); if (el) el.classList.add('on');
  document.querySelectorAll('.tool[data-v]').forEach(b => b.classList.toggle('on', b.dataset.v === v));
}
function toggleTheme(){
  state.theme = state.theme === 'light' ? 'dark' : 'light';
  document.body.dataset.theme = state.theme;
  document.getElementById('themeBtn').textContent = state.theme === 'light' ? '浅色' : '深色';
}
function toggleMode(){
  state.mode = state.mode === 'grid' ? 'single' : 'grid';
  document.getElementById('modeBtn').textContent = state.mode === 'grid' ? '平铺' : '单屏';
  renderGrid();
}
function onQ(v){ state.q = (v || '').trim().toLowerCase(); renderGrid(); }

function renderTags(){
  const box = document.getElementById('gtags');
  let h = `<span class="gtag ${state.group === 'ALL' ? 'on' : ''}" onclick="setGroup('ALL')">全部 · <b>${SHOTS.length}</b></span>`;
  for (const g of GROUPS){
    const n = SHOTS.filter(s => s.g === g.id).length;
    h += `<span class="gtag ${state.group === g.id ? 'on' : ''}" onclick="setGroup('${g.id}')">${g.id} ${g.name} · ${n}</span>`;
  }
  box.innerHTML = h;
}
function setGroup(g){ state.group = g; renderTags(); renderGrid(); }

function shotCard(s){
  let html = '';
  try { html = s.render(); }
  catch (e) { html = `<div class="phone"><div class="empty" style="color:#DC2626">渲染失败：${esc(e.message)}</div></div>`; }
  const meta = [];
  if (s.state)   meta.push(`<b>运行态</b> ${esc(s.state)}`);
  if (s.trigger) meta.push(`<b>触发</b> ${esc(s.trigger)}`);
  if (s.spec)    meta.push(`<span class="src"><b>依据</b> ${esc(s.spec)}</span>`);
  return `<div class="shot" data-id="${esc(s.id)}">
    <div class="shot-hd"><span class="shot-id">${esc(s.id)}</span><span class="shot-nm">${esc(s.name)}</span></div>
    <div onclick="openLb('${esc(s.id)}')" style="cursor:zoom-in">${html}</div>
    <div class="shot-meta">${meta.join(' &nbsp;·&nbsp; ')}</div>
  </div>`;
}
function match(s){
  if (state.group !== 'ALL' && s.g !== state.group) return false;
  const q = String(state.q || '').toLowerCase();
  if (!q) return true;
  return [s.id, s.name, s.state, s.trigger, s.spec].join(' ').toLowerCase().includes(q);
}
function renderGrid(){
  const list = SHOTS.filter(match);
  const grid = document.getElementById('grid');
  grid.innerHTML = list.map(shotCard).join('') || `<p class="note">没有匹配的形态图。</p>`;
  grid.style.gridTemplateColumns = state.mode === 'single' ? 'repeat(auto-fill,minmax(268px,1fr))' : '';
  const n = list.length, groups = new Set(list.map(s => s.g)).size;
  document.getElementById('figNote').innerHTML =
    `共 <b>${SHOTS.length}</b> 张代码绘制的形态图，当前显示 <b>${n}</b> 张 / ${groups} 组。` +
    `每张只表达<b>一个状态</b>；界面里只允许出现「状态 · 书名 · 章名 · 操作名 · 数字」，` +
    `<b>禁止出现解释原理 / 后果 / 用法的教程型文字</b>。点开可放大。`;
}
function openLb(id){
  const s = SHOTS.find(x => x.id === id); if (!s) return;
  let html = ''; try { html = s.render(); } catch (e) { html = esc(e.message); }
  document.getElementById('lbIn').innerHTML = html;
  document.getElementById('lbCap').textContent = `${s.id} · ${s.name}${s.state ? ' · ' + s.state : ''}`;
  document.getElementById('lb').classList.add('on');
}
function closeLb(){ document.getElementById('lb').classList.remove('on'); }
document.addEventListener('keydown', e => { if (e.key === 'Escape') closeLb(); });

renderTags();
renderGrid();
document.body.dataset.theme = state.theme;
document.getElementById('contractDoc').innerHTML = DOCS.contract;
document.getElementById('checkDoc').innerHTML = DOCS.check;
