/* ==========================================================================
   声页重做 · 新版界面规格 · 公共绘制库 U（纯 HTML/CSS/SVG，不使用任何图片）
   --------------------------------------------------------------------------
   本文件是「总工程师」定义的唯一共同语言。所有子智能体只允许调用这里
   暴露的 API，禁止自行发明组件、禁止内联色值、禁止引入外部资源。
   API 清单与用法见 SPEC_CONTRACT.md。
   ========================================================================== */

/* ---------- 组定义 / 图册容器 ---------- */
const GROUPS = [
  {id:'A', name:'我的书',       desc:'首页 · 资产总览'},
  {id:'B', name:'这一本',       desc:'目录页 · 只管内容与章节'},
  {id:'C', name:'正在做',       desc:'任务页 · 全 App 唯一状态中心'},
  {id:'D', name:'续跑与开做',   desc:'启动弹窗 / 开做之前确认卡'},
  {id:'E', name:'正在听',       desc:'播放器与队列'},
  {id:'F', name:'调音台',       desc:'保留结构，玻璃化'},
  {id:'G', name:'设置与模型',   desc:'重分层 / 小修'},
  {id:'H', name:'系统态',       desc:'通知 / 悬浮球 / 权限'},
];
const SHOTS = [];
const S = (o) => SHOTS.push(o);

/* ---------- 图标（自绘 path，24×24 stroke） ---------- */
const U = {
  svg(paths, size){return `<svg viewBox="0 0 24 24" width="${size||16}" height="${size||16}" fill="none" stroke="currentColor" stroke-width="1.9" stroke-linecap="round" stroke-linejoin="round">${paths}</svg>`},
  I:{
    back:'<path d="M15 18l-6-6 6-6"/>',
    menu:'<path d="M4 6h16M4 12h16M4 18h16"/>',
    plus:'<path d="M12 5v14M5 12h14"/>',
    more:'<circle cx="5" cy="12" r="1"/><circle cx="12" cy="12" r="1"/><circle cx="19" cy="12" r="1"/>',
    book:'<path d="M4 5.5A2.5 2.5 0 016.5 3H20v16H6.5A2.5 2.5 0 004 21.5z"/><path d="M4 18.5A2.5 2.5 0 016.5 16H20"/>',
    tasks:'<path d="M9 6h11M9 12h11M9 18h11"/><path d="M4 6l1 1 2-2M4 12l1 1 2-2M4 18l1 1 2-2"/>',
    play:'<path d="M8 5l11 7-11 7z"/>',
    pause:'<path d="M9 5v14M15 5v14"/>',
    next:'<path d="M6 5l10 7-10 7z"/><path d="M18 5v14"/>',
    prev:'<path d="M18 5L8 12l10 7z"/><path d="M6 5v14"/>',
    mixer:'<path d="M4 7h10M18 7h2M4 12h2M10 12h10M4 17h8M16 17h4"/><circle cx="16" cy="7" r="2"/><circle cx="8" cy="12" r="2"/><circle cx="14" cy="17" r="2"/>',
    gear:'<circle cx="12" cy="12" r="3"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/>',
    moon:'<path d="M20 14A8 8 0 019.9 4 8 8 0 1020 14z"/>',
    sun:'<circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M2 12h2M20 12h2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/>',
    check:'<path d="M5 12l5 5L20 7"/>',
    trash:'<path d="M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13"/>',
    chev:'<path d="M9 6l6 6-6 6"/>',
    search:'<circle cx="11" cy="11" r="6"/><path d="M16 16l4 4"/>',
    head:'<path d="M4 14v-2a8 8 0 0116 0v2"/><path d="M4 14h3v6H5a1 1 0 01-1-1zM20 14h-3v6h2a1 1 0 001-1z"/>',
    clock:'<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
    list:'<path d="M8 6h12M8 12h12M8 18h12M4 6h.01M4 12h.01M4 18h.01"/>',
    volume:'<path d="M5 9v6h4l5 4V5L9 9z"/><path d="M17 9a4 4 0 010 6"/>',
    warn:'<path d="M12 3l9 16H3z"/><path d="M12 9v5M12 17h.01"/>',
    download:'<path d="M12 3v12M7 11l5 5 5-5M4 20h16"/>',
    refresh:'<path d="M20 11a8 8 0 10-2 6M20 5v6h-6"/>',
    x:'<path d="M6 6l12 12M18 6L6 18"/>',
    pin:'<path d="M9 3h6l-1 6 3 3v2H7v-2l3-3z"/><path d="M12 14v7"/>',
  },
  ic(n,cls){return `<span class="ic ${cls||''}">${U.svg(U.I[n]||U.I.menu)}</span>`},

  /* ---------- 骨架 ---------- */
  sb(){return `<div class="statusbar"><span>9:41</span><span class="sb-r">${U.svg('<path d="M2 8h3v8H2zM7 6h3v10H7zM12 4h3v12h-3zM17 2h3v14h-3z"/>',11)} ${U.svg('<path d="M12 18a2 2 0 100 4 2 2 0 000-4zM12 4c4 0 7 3 7 7M12 8c2 0 4 2 4 4"/>',11)} ${U.svg('<rect x="2" y="7" width="17" height="10" rx="2"/><path d="M21 10v4"/>',13)}</span></div>`},

  /* 顶栏：大标题 + 可选副标题 + 右侧文字按钮（新版禁止纯图标无文字） */
  bar(o){o=o||{};const tr=(o.trailing||[]).map(t=>`<span class="ab-t">${t}</span>`).join('');
    return `<div class="appbar">
      ${o.back?`<span class="ab-back">${U.svg(U.I.back,20)}</span>`:''}
      <div class="ab-txt"><div class="ab-title">${o.title||''}</div>${o.sub?`<div class="ab-sub">${o.sub}</div>`:''}</div>
      <div class="ab-right">${tr}</div></div>`;

  },
  /* 底部四空间 tab（新版核心导航） */
  tabs(on){const L=[['book','我的书'],['list','这一本'],['tasks','正在做'],['head','正在听']];
    return `<div class="tabbar">${L.map((x,i)=>`<div class="tab ${i===on?'on':''}">${U.svg(U.I[x[0]],20)}<span>${x[1]}</span></div>`).join('')}</div>`;
  },
  /* 迷你播放条（在 tab 之上） */
  mini(o){o=o||{};return `<div class="minibar"><span class="mb-cover">♪</span>
    <div class="mb-txt"><b>${o.title||'未在播放'}</b><span>${o.sub||'—'}</span></div>
    <span class="mb-go">${U.svg(U.I[o.playing?'pause':'play'],18)}</span></div>`;
  },
  /* 手机：内容区负责滚动；mini/tabs 常驻 */
  phone(o){o=o||{};return `<div class="phone">
      <div class="phone-bg"><i class="o1"></i><i class="o2"></i><i class="o3"></i></div>
      ${U.sb()}
      ${o.appbar||''}
      <div class="content">${o.body||''}</div>
      ${o.mini&&o.mini!==false?o.mini:''}
      ${o.tabs===false?'':U.tabs(o.tab==null?0:o.tab)}
    </div>`;
  },

  /* ---------- 基础原子 ---------- */
  C(inner,cls){return `<div class="card ${cls||''}">${inner}</div>`},
  glass(inner,cls){return `<div class="card glass ${cls||''}">${inner}</div>`},
  R(inner,cls){return `<div class="row ${cls||''}">${inner}</div>`},
  T(t,cls){return `<span class="t ${cls||''}">${t}</span>`},
  B(t,cls){return `<span class="btn ${cls||''}">${t}</span>`},
  chip(t,cls){return `<span class="chip ${cls||''}">${t}</span>`},
  bar2(pct,cls){return `<div class="bar ${cls||''}"><i style="width:${pct}%"></i></div>`},
  kv(k,v){return `<div class="kv"><span>${k}</span><span class="v">${v}</span></div>`},
  divider(){return '<div class="divider"></div>'},
  spin(w){return `<span class="spin ${w?'w':''}"></span>`},
  dim(inner){return `<div class="dim">${inner}</div>`},
  dlg(o){return U.dim(`<div class="dlg"><h4>${o.title||''}</h4>
    ${o.text?`<p>${o.text}</p>`:''}
    ${o.body||''}
    <div class="dlg-btns">${(o.buttons||[]).map((b,i)=>`<span class="btn ${i===0?'pri':'ghost'}">${b}</span>`).join('')}</div></div>`);
  },
  menu(o){return `<div class="menu" style="${o.style||'top:150px;left:80px'}">${(o.items||[]).map(i=>`<div>${i}</div>`).join('')}</div>`;
  },
  toast(t){return `<div class="toast">${t}</div>`},
  ball(o){return `<div class="ball" style="right:12px;top:${o.top||300}px;--v:${(o.v||0)*3.6}deg"><b>${o.v||0}</b>${o.sub?`<span>${o.sub}</span>`:''}</div>`;
  },
  notif(o){return `<div class="notif"><div class="nh"><span>声页</span><span>${o.time||'现在'}</span></div>
    <div class="nb"><b>${o.title||''}</b><p>${o.text||''}</p>${o.action?`<span class="na">${o.action}</span>`:''}</div></div>`;
  },
  field(t,ph,area){return `<div class="field ${area?'area':''}"><span class="${t?'':'ph'}">${t||ph}</span></div>`;
  },
  sw(on){return `<span class="switch ${on?'on':''}"></span>`},
  empty(t,btns){return `<div class="empty"><div class="ec">♪</div><div>${t}</div>
    ${(btns||[]).map(b=>`<span class="btn ghost sm">${b}</span>`).join('')}</div>`;
  },
  skel(n){let s='';for(let i=0;i<(n||3);i++)s+=`<div class="skel"><span class="sbox"></span><div class="sl"><i></i><i></i></div></div>`;return s;
  },
  slider(label,val,unit){return `<div class="slider"><div class="sl-h"><span>${label}</span><b>${val}${unit||''}</b></div><div class="sl-t"><i style="width:${val}%"></i><em style="left:${val}%"></em></div></div>`;
  },
  /* 状态环：K 组样板 —— 只留数字，不写字 */
  ring(v,sub){return `<div class="ring" style="--v:${(v||0)*3.6}deg"><div><b>${v||0}</b><span>${sub||'%'}</span></div></div>`;
  },
  /* 大标题区（我的书 / 正在做 用） */
  hero(title,sub){return `<div class="hero"><h2>${title}</h2>${sub?`<p>${sub}</p>`:''}</div>`;
  },
  /* 状态卡：新版唯一进度口径，一根条一个百分比 */
  stat(o){return U.glass(`<div class="row"><span class="t b">${o.title}</span><span class="grow"></span><span class="t p b">${o.pct}%</span></div>
    ${o.line?`<div class="t s" style="margin:4px 0 8px">${o.line}</div>`:'<div style="height:8px"></div>'}
    ${U.bar2(o.pct)}
    ${o.actions?`<div class="row" style="margin-top:10px">${o.actions.map((a,i)=>`<span class="btn ${i?'ghost':'line'} sm">${a}</span>`).join('')}<span class="grow"></span></div>`:''}`);
  },
  /* 书 / 章 / 曲目行 */
  track(o){return U.glass(`<div class="row"><span class="tk-ic">${U.svg(U.I[o.icon||'book'],18)}</span>
    <div class="tk-txt"><b>${o.title}</b><span>${o.sub||''}</span></div>
    <span class="grow"></span>${o.right||''}</div>`, o.cls||'');
  },
  /* 一张卡包多行（inset grouped）：书单、章节列表、设置分组都用它。
     禁止一个列表每行各套一张卡 —— 卡片堆卡片，层级全塌。 */
  list(rows,cls){return `<div class="card ${cls||'list'}">${(rows||[]).filter(Boolean).join('<div class="divider"></div>')}</div>`;
  },
  /* 列表行：书名 + 元信息 + 可选进度 + 右侧状态。无图标，纯文字。 */
  trk(o){return `<div class="trk"><div class="tk-txt"><b>${o.title}</b>
    <span>${o.sub||''}</span>${o.pct!=null?U.bar2(o.pct):''}</div>
    <span class="grow"></span>${o.right||''}</div>`;
  },
  /* 章节状态胶囊（统一词表，只允许 SPEC_CONTRACT.md 里的词） */
  stateChip(kind){const M={done:['可听','g'],running:['制作中','w'],queued:['排队中','line'],idle:['未开始','line'],failed:['失败','r'],paused:['已暂停','line']};
    const x=M[kind]||M.idle;return `<span class="chip ${x[1]}">${x[0]}</span>`;
  },
  /* 小块网格（制作进度细览，收进任务页而非目录页） */
  segGrid(spec){const CL={done:'sd',run:'sr',redo:'sx',wait:'sw'};
    return `<div class="seggrid">${(spec||[]).map((s,i)=>`<i class="${CL[s.c]||'sw'}">${s.t||(i+1)}</i>`).join('')}</div>`;
  },
  legend(){return `<div class="legend"><span><i class="sd"></i>已生成</span><span><i class="sr"></i>合成中</span><span><i class="sx"></i>需重做</span><span><i class="sw"></i>待生成</span></div>`;
  },
  voices(names,on){return `<div class="voice-grid">${(names||[]).map((n,i)=>`<div class="${i===on?'on':''}">${n}</div>`).join('')}</div>`;
  },
  modes(labels,on){return `<div class="modes">${(labels||[]).map((l,i)=>`<div class="${i===on?'on':''}">${l}</div>`).join('')}</div>`;
  },
  cover(o){return `<div class="cover ${o&&o.small?'sm':''}">${(o&&o.txt)||'♪'}</div>`;
  },
  playctl(o){return `<div class="playctl"><span>${U.svg(U.I.prev,22)}</span>
    <span class="pmain">${U.svg(U.I[o&&o.playing?'pause':'play'],26)}</span>
    <span>${U.svg(U.I.next,22)}</span></div>`;
  },
};

/* 快捷包装：默认带 mini 与 tab；子智能体统一用 P() 画一屏 */
const P = (o) => U.phone(o);
