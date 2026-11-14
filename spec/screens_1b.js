S({
  id:'B1', g:'B', name:'这一本 · 正常态', state:'可听', trigger:'打开这一本',
  spec:'ARCHITECTURE feature:book',
  render: ()=>P({
    appbar: U.bar({back:true, title:'小王子', sub:'共 6 章', trailing:['更多']}),
    body: '<div class="scroll">' +
      U.stat({title:'可听', pct:66, line:'4 / 6', actions:['接着听','制作这一本']}) +
      U.divider() +
      [['第一章 · 初到沙漠','8:12','done','收听','play'],
       ['第二章 · 沙漠里的狐狸','12:34','done','收听','play'],
       ['第三章 · 玫瑰','9:05','done','收听','play'],
       ['第四章 · 星球','10:41','done','收听','play'],
       ['第五章 · 落日','7:28','running','阅读','book'],
       ['第六章 · 归途','6:50','idle','阅读','book']]
        .map(c=>U.track({title:c[0], sub:c[1], icon:c[4], right:U.stateChip(c[2])+U.B(c[3],'ghost sm')})).join('') +
      '</div>',
    tab:1, mini:false
  })
});

S({
  id:'B2', g:'B', name:'这一本 · 制作中', state:'制作中', trigger:'有章在制作',
  spec:'ARCHITECTURE feature:book',
  render: ()=>P({
    appbar: U.bar({back:true, title:'小王子', sub:'共 6 章', trailing:['更多']}),
    body: '<div class="scroll">' +
      U.stat({title:'制作中', pct:50, line:'3 / 6', actions:['接着听','制作这一本']}) +
      U.divider() +
      [['第一章 · 初到沙漠','8:12','done','收听','play'],
       ['第二章 · 沙漠里的狐狸','12:34','done','收听','play'],
       ['第三章 · 玫瑰','9:05','done','收听','play'],
       ['第四章 · 星球','10:41','running','阅读','book'],
       ['第五章 · 落日','7:28','idle','阅读','book'],
       ['第六章 · 归途','6:50','idle','阅读','book']]
        .map(c=>U.track({title:c[0], sub:c[1], icon:c[4], right:U.stateChip(c[2])+U.B(c[3],'ghost sm')})).join('') +
      '</div>',
    tab:1, mini:false
  })
});

S({
  id:'B3', g:'B', name:'这一本 · 全部可听', state:'可听', trigger:'全部章可听',
  spec:'ARCHITECTURE feature:book',
  render: ()=>P({
    appbar: U.bar({back:true, title:'小王子', sub:'共 6 章', trailing:['更多']}),
    body: '<div class="scroll">' +
      U.stat({title:'可听', pct:100, line:'6 / 6', actions:['接着听','重新分章']}) +
      U.divider() +
      [['第一章 · 初到沙漠','8:12','done','收听','play'],
       ['第二章 · 沙漠里的狐狸','12:34','done','收听','play'],
       ['第三章 · 玫瑰','9:05','done','收听','play'],
       ['第四章 · 星球','10:41','done','收听','play'],
       ['第五章 · 落日','7:28','done','收听','play'],
       ['第六章 · 归途','6:50','done','收听','play']]
        .map(c=>U.track({title:c[0], sub:c[1], icon:c[4], right:U.stateChip(c[2])+U.B(c[3],'ghost sm')})).join('') +
      '</div>',
    tab:1, mini:false
  })
});

S({
  id:'B4', g:'B', name:'这一本 · 全部未开始', state:'未开始', trigger:'新导入未开做',
  spec:'ARCHITECTURE feature:book',
  render: ()=>P({
    appbar: U.bar({back:true, title:'小王子', sub:'共 6 章', trailing:['更多']}),
    body: '<div class="scroll">' +
      U.stat({title:'未开始', pct:0, line:'0 / 6', actions:['制作这一本','导入']}) +
      U.divider() +
      [['第一章 · 初到沙漠','8:12','idle','阅读','book'],
       ['第二章 · 沙漠里的狐狸','12:34','idle','阅读','book'],
       ['第三章 · 玫瑰','9:05','idle','阅读','book'],
       ['第四章 · 星球','10:41','idle','阅读','book'],
       ['第五章 · 落日','7:28','idle','阅读','book'],
       ['第六章 · 归途','6:50','idle','阅读','book']]
        .map(c=>U.track({title:c[0], sub:c[1], icon:c[4], right:U.stateChip(c[2])+U.B(c[3],'ghost sm')})).join('') +
      '</div>',
    tab:1, mini:false
  })
});

S({
  id:'B5', g:'B', name:'这一本 · 接着听', state:'可听', trigger:'有收听位置',
  spec:'ARCHITECTURE feature:book',
  render: ()=>P({
    appbar: U.bar({back:true, title:'小王子', sub:'共 6 章', trailing:['更多']}),
    body: '<div class="scroll">' +
      U.stat({title:'可听', pct:66, line:'4 / 6 · 上次听到第二章 12:34', actions:['接着听','制作这一本']}) +
      U.divider() +
      [['第一章 · 初到沙漠','8:12','done','收听','play'],
       ['第二章 · 沙漠里的狐狸','12:34','done','接着听','play'],
       ['第三章 · 玫瑰','9:05','done','收听','play'],
       ['第四章 · 星球','10:41','done','收听','play'],
       ['第五章 · 落日','7:28','done','收听','play'],
       ['第六章 · 归途','6:50','done','收听','play']]
        .map(c=>U.track({title:c[0], sub:c[1], icon:c[4], right:U.stateChip(c[2])+U.B(c[3],'ghost sm')})).join('') +
      '</div>',
    tab:1, mini:false
  })
});

S({
  id:'B6', g:'B', name:'阅读器 · 全屏', state:'可听', trigger:'点阅读入口',
  spec:'ARCHITECTURE feature:book',
  render: ()=>P({
    appbar: U.bar({back:true, title:'第二章 · 沙漠里的狐狸', sub:'小王子', trailing:['目录']}),
    body: '<div class="scroll">' +
      U.C(
        U.R(U.T('天亮时，沙漠露出浅金的边。','s')) +
        U.R(U.T('他把水囊递过去，没说话。','s')) +
        U.R(U.T('远处有一列骆驼，慢慢挪过沙丘。','s')) +
        U.R(U.T('风停了，四周安静得像一张纸。','s')) +
        U.R(U.T('她数到第七颗星，才合上本子。','s'))
      ) +
      U.C(
        U.R(U.T('字号','s') + '<span class="grow"></span>' + U.B('A-','line sm') + U.T('16px','b') + U.B('A+','line sm')) +
        U.divider() +
        U.R(U.B('上一章','line') + '<span class="grow"></span>' + U.B('下一章','line'))
      ) +
      '</div>',
    tabs:false, mini:false
  })
});

S({
  id:'B7', g:'B', name:'重新分章 · 确认', state:'可听', trigger:'点重新分章',
  spec:'CONSTITUTION 内容与导入',
  render: ()=>P({
    appbar: U.bar({back:true, title:'小王子', sub:'共 6 章', trailing:['更多']}),
    body: '<div class="scroll">' +
      U.stat({title:'可听', pct:66, line:'4 / 6', actions:['接着听','重新分章']}) +
      U.divider() +
      [['第一章 · 初到沙漠','8:12','done','收听','play'],
       ['第二章 · 沙漠里的狐狸','12:34','done','收听','play'],
       ['第三章 · 玫瑰','9:05','done','收听','play'],
       ['第四章 · 星球','10:41','done','收听','play'],
       ['第五章 · 落日','7:28','running','阅读','book'],
       ['第六章 · 归途','6:50','idle','阅读','book']]
        .map(c=>U.track({title:c[0], sub:c[1], icon:c[4], right:U.stateChip(c[2])+U.B(c[3],'ghost sm')})).join('') +
      '</div>' +
      U.dlg({title:'重新分章', text:'共 6 章 · 已制作的音频会清掉', buttons:['确认','取消']}),
    tab:1, mini:false
  })
});

S({
  id:'B8', g:'B', name:'单章 · 长按菜单', state:'可听', trigger:'长按单章',
  spec:'ARCHITECTURE feature:book',
  render: ()=>P({
    appbar: U.bar({back:true, title:'小王子', sub:'共 6 章', trailing:['更多']}),
    body: '<div class="scroll">' +
      U.stat({title:'可听', pct:66, line:'4 / 6', actions:['接着听','制作这一本']}) +
      U.divider() +
      [['第一章 · 初到沙漠','8:12','done','收听','play'],
       ['第二章 · 沙漠里的狐狸','12:34','done','收听','play'],
       ['第三章 · 玫瑰','9:05','done','收听','play'],
       ['第四章 · 星球','10:41','done','收听','play'],
       ['第五章 · 落日','7:28','running','阅读','book'],
       ['第六章 · 归途','6:50','idle','阅读','book']]
        .map(c=>U.track({title:c[0], sub:c[1], icon:c[4], right:U.stateChip(c[2])+U.B(c[3],'ghost sm')})).join('') +
      '</div>' +
      U.menu({items:['收听','阅读','制作这一章'], style:'top:300px;left:90px'}),
    tab:1, mini:false
  })
});
