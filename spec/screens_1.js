/* A 组 · 我的书（首页）
 *
 * 硬规矩（总工程师 2026-11-15 真机验收后定，违反重画）：
 * 1. 不写页面大标题。「我的书」四个字底部 tab 里已经有了，顶栏再写一遍是重复。
 * 2. 粘贴区不写标题。输入框的 placeholder 已经在说「在这里粘贴」。
 * 3. 书单是一张卡包多行，不是一个列表每行套一张卡。
 * 4. 首页只有两样东西：能粘的地方、书单。不要统计数字四件套。
 */

const PASTE = (txt) =>
  U.glass(
    U.field(txt || '', '在这里粘贴…', true) +
    U.R(U.B('导入文件', 'line sm') + '<span class="grow"></span>' + U.B('合成', txt ? 'pri sm' : 'pri sm off'))
  );

S({
  id: 'A1', g: 'A', name: '首页 · 空态',
  state: '空', trigger: '首次安装',
  spec: 'CONSTITUTION 内容与导入 / ARCHITECTURE feature:library',
  render: () => P({
    mini: false, tab: 0,
    body: `<div class="scroll" style="padding-top:10px">` +
      PASTE() +
      U.empty('还没有书') +
      `</div>`
  })
});

S({
  id: 'A2', g: 'A', name: '首页 · 三本书',
  state: '可听', trigger: '已有藏书',
  spec: 'CONSTITUTION 内容与导入 / ARCHITECTURE feature:library',
  render: () => P({
    mini: false, tab: 0,
    body: `<div class="scroll" style="padding-top:10px">` +
      PASTE() +
      U.list([
        U.trk({ title: '百年孤独', sub: '32 章', right: U.stateChip('done') }),
        U.trk({ title: '小王子', sub: '12 章', right: U.stateChip('idle') }),
        U.trk({ title: '人类简史', sub: '48 章', right: U.stateChip('idle') }),
      ]) +
      `</div>`
  })
});

S({
  id: 'A3', g: 'A', name: '首页 · 有在做的',
  state: '制作中', trigger: '开做之后',
  spec: 'CONSTITUTION 内容与导入 / ARCHITECTURE feature:library',
  render: () => P({
    mini: false, tab: 0,
    body: `<div class="scroll" style="padding-top:10px">` +
      PASTE() +
      U.list([
        U.trk({ title: '小王子', sub: '第 3 / 12 章 · 41%', pct: 41, right: U.stateChip('running') }),
        U.trk({ title: '百年孤独', sub: '32 章 · 听完 5 章', pct: 16, right: U.stateChip('done') }),
        U.trk({ title: '人类简史', sub: '48 章', right: U.stateChip('idle') }),
      ]) +
      `</div>`
  })
});

S({
  id: 'A4', g: 'A', name: '首页 · 全部可听',
  state: '可听', trigger: '全部完成',
  spec: 'CONSTITUTION 内容与导入 / ARCHITECTURE feature:library',
  render: () => P({
    mini: false, tab: 0,
    body: `<div class="scroll" style="padding-top:10px">` +
      PASTE() +
      U.list([
        U.trk({ title: '百年孤独', sub: '32 章 · 听完 12 章', pct: 38, right: U.stateChip('done') }),
        U.trk({ title: '小王子', sub: '12 章 · 听完 12 章', pct: 100, right: U.stateChip('done') }),
        U.trk({ title: '人类简史', sub: '48 章', right: U.stateChip('done') }),
      ]) +
      `</div>`
  })
});

S({
  id: 'A5', g: 'A', name: '首页 · 导入中',
  state: '导入中', trigger: '选了一个大文件',
  spec: 'CONSTITUTION 内容与导入 / STATE_VOCAB §1.5 导入态',
  render: () => P({
    mini: false, tab: 0,
    body: `<div class="scroll" style="padding-top:10px">` +
      PASTE() +
      U.list([
        U.trk({ title: '三体', sub: '正在读入…', right: U.spin() + U.chip('导入中', 'w') }),
        U.trk({ title: '活着', sub: '文件读不进来', right: U.chip('导入失败', 'r') + U.B('重试', 'sm') }),
        U.trk({ title: '百年孤独', sub: '32 章', right: U.stateChip('done') }),
      ]) +
      `</div>`
  })
});

S({
  id: 'A6', g: 'A', name: '首页 · 粘贴了文字',
  state: '待生成', trigger: '粘完一段',
  spec: 'CONSTITUTION 内容与导入 / 首页 1/3 粘贴区',
  render: () => P({
    mini: false, tab: 0,
    body: `<div class="scroll" style="padding-top:10px">` +
      PASTE('那天下着小雨，我站在书店门口，看见玻璃上自己的影子。') +
      U.list([
        U.trk({ title: '活着', sub: '6 章 · 听完 4 章', pct: 66, right: U.stateChip('running') }),
      ]) +
      `</div>`
  })
});

S({
  id: 'A7', g: 'A', name: '片段 · 三态',
  state: '合成中', trigger: '片段合成',
  spec: 'CONSTITUTION 内容与导入',
  render: () => P({
    mini: false, tab: 0,
    body: `<div class="scroll" style="padding-top:10px">` +
      PASTE() +
      U.T('片段 · 3', 's') +
      U.list([
        U.trk({ title: '那天下着小雨…', sub: '0:42', right: U.spin() + U.chip('合成中', 'w') }),
        U.trk({ title: '会议纪要第三段', sub: '1:18', right: U.chip('已生成', 'g') }),
        U.trk({ title: '开头那段', sub: '0:27', right: U.chip('需重做', 'r') + U.B('重做', 'sm') }),
      ]) +
      `</div>`
  })
});

S({
  id: 'A8', g: 'A', name: '长按 · 删除确认',
  state: '可听', trigger: '长按一本书',
  spec: 'CONSTITUTION 内容与导入 / ACCEPTANCE B7',
  render: () => P({
    mini: false, tab: 0,
    body: `<div class="scroll" style="padding-top:10px">` +
      PASTE() +
      U.list([
        U.trk({ title: '活着', sub: '20 章', right: U.stateChip('done') }),
        U.trk({ title: '三体', sub: '30 章', right: U.stateChip('done') }),
      ]) +
      `</div>` +
      U.menu({ items: ['置顶', '重命名', '删除'], style: 'top:170px;left:120px' }) +
      U.dlg({ title: '删除《活着》', text: '不可撤销', buttons: ['删除', '取消'] })
  })
});
