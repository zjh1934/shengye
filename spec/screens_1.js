S({
  id:'A1', g:'A', name:'首页 · 空态',
  state:'未开始', trigger:'首次安装',
  spec:'CONSTITUTION 内容与导入 / ARCHITECTURE feature:library',
  render: ()=>P({
    appbar: U.bar({title:'我的书'}),
    mini:false, tab:0,
    body: `<div class="scroll">`+
      U.glass(U.T('粘贴一段','b')+U.field('','粘贴或输入要听的内容',true)+U.R('<span class="grow"></span>'+U.B('合成这段','pri sm')))+
      U.empty('还没有书 · 支持 txt / md / epub',['导入'])+
      `</div>`
  })
});

S({
  id:'A2', g:'A', name:'首页 · 三本书',
  state:'可听', trigger:'已有藏书',
  spec:'CONSTITUTION 内容与导入 / ARCHITECTURE feature:library',
  render: ()=>P({
    appbar: U.bar({title:'我的书'}),
    mini:false, tab:0,
    body: `<div class="scroll">`+
      U.glass(U.T('粘贴一段','b')+U.field('','粘贴或输入要听的内容',true)+U.R('<span class="grow"></span>'+U.B('合成这段','pri sm')))+
      U.T('我的书 · 3 本','s')+
      U.track({title:'百年孤独', sub:'32章', icon:'book', right:U.stateChip('done')})+
      U.track({title:'小王子', sub:'12章', icon:'book', right:U.stateChip('running')})+
      U.track({title:'人类简史', sub:'48章', icon:'book', right:U.stateChip('idle')})+
      `</div>`
  })
});

S({
  id:'A3', g:'A', name:'首页 · 制作中',
  state:'制作中', trigger:'开做之后',
  spec:'CONSTITUTION 内容与导入 / ARCHITECTURE feature:library',
  render: ()=>P({
    appbar: U.bar({title:'我的书'}),
    mini:false, tab:0,
    body: `<div class="scroll">`+
      U.C(U.kv('可听','2')+U.kv('制作中','1'))+
      U.T('制作中','s')+
      U.track({title:'小王子', sub:'12章', right:U.stateChip('running')})+
      U.T('可听','s')+
      U.track({title:'百年孤独', sub:'32章', right:U.stateChip('done')})+
      U.track({title:'人类简史', sub:'48章', right:U.stateChip('done')})+
      `</div>`
  })
});

S({
  id:'A4', g:'A', name:'首页 · 全部可听',
  state:'可听', trigger:'全部完成',
  spec:'CONSTITUTION 内容与导入 / ARCHITECTURE feature:library',
  render: ()=>P({
    appbar: U.bar({title:'我的书'}),
    mini:false, tab:0,
    body: `<div class="scroll">`+
      U.T('可听','s')+
      U.track({title:'百年孤独', sub:'32章', right:U.stateChip('done')})+
      U.track({title:'小王子', sub:'12章', right:U.stateChip('done')})+
      U.track({title:'人类简史', sub:'48章', right:U.stateChip('done')})+
      `</div>`
  })
});

S({
  id:'A5', g:'A', name:'首页 · 导入',
  state:'失败', trigger:'导入这本书',
  spec:'CONSTITUTION 内容与导入 / ARCHITECTURE feature:library',
  render: ()=>P({
    appbar: U.bar({title:'我的书'}),
    mini:false, tab:0,
    body: `<div class="scroll">`+
      U.T('导入','s')+
      U.track({title:'三体', right:U.spin()+U.chip('导入中','w')})+
      U.track({title:'活着', right:U.stateChip('failed')+U.B('重试','sm')})+
      `</div>`
  })
});

S({
  id:'A6', g:'A', name:'念一段 · 展开',
  state:'待生成', trigger:'展开念一段',
  spec:'CONSTITUTION 内容与导入 / ARCHITECTURE feature:library',
  render: ()=>P({
    appbar: U.bar({title:'我的书'}),
    mini:false, tab:0,
    body: `<div class="scroll">`+
      U.T('念一段','s')+
      U.C(
        U.field('','输入文字',true)+
        U.divider()+
        U.kv('状态','待生成')
      )+
      U.R(U.B('合成这段','pri'))+
      `</div>`
  })
});

S({
  id:'A7', g:'A', name:'片段 · 三态',
  state:'合成中', trigger:'片段合成',
  spec:'CONSTITUTION 内容与导入 / ARCHITECTURE feature:library',
  render: ()=>P({
    appbar: U.bar({title:'我的书'}),
    mini:false, tab:0,
    body: `<div class="scroll">`+
      U.T('片段 · 3','s')+
      U.track({title:'片段1', right:U.spin()+U.chip('合成中','w')})+
      U.track({title:'片段2', right:U.chip('已生成','g')})+
      U.track({title:'片段3', right:U.chip('需重做','r')+U.B('重做','sm')})+
      `</div>`
  })
});

S({
  id:'A8', g:'A', name:'长按 · 删除确认',
  state:'可听', trigger:'长按一本书',
  spec:'CONSTITUTION 内容与导入 / ARCHITECTURE feature:library',
  render: ()=>P({
    appbar: U.bar({title:'我的书'}),
    mini:false, tab:0,
    body: `<div class="scroll">`+
      U.track({title:'活着', sub:'20章', right:U.stateChip('done')})+
      U.track({title:'三体', sub:'30章', right:U.stateChip('done')})+
      `</div>`+
      U.menu({items:['置顶','重命名','删除'], style:'top:170px;left:120px'})+
      U.dlg({title:'删除《活着》', text:'不可撤销', buttons:['删除','取消']})
  })
});
