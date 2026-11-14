S({id:'G1',g:'G',name:'设置 · 运行状态',state:'已完成',trigger:'从我的书进入设置',spec:'CONSTITUTION 模型与系统 / ARCHITECTURE feature:settings',
render:()=>P({
  tab:0,
  appbar:U.bar({back:true,title:'设置'}),
  body:`<div class="scroll">
    ${U.R(U.T('运行状态','s'))}
    ${U.track({title:'模型',sub:'4 / 4 已就绪',icon:'gear',right:U.chip('就绪','g')})}
    ${U.track({title:'权限',sub:'麦克风 · 通知 · 存储',icon:'head',right:U.chip('已开启','g')})}
    ${U.track({title:'存储',sub:'12.4 GB / 64 GB',icon:'book',right:U.chip('充足','line')})}
    ${U.R(U.T('其他','s'))}
    ${U.track({title:'外观',sub:'随系统',icon:'moon',right:U.ic('chev')})}
    ${U.track({title:'我的音频',sub:'3 项 · 1.2 GB',icon:'head',right:U.ic('chev')})}
    ${U.track({title:'开发者工具',sub:'',icon:'list',right:U.ic('chev')})}
  </div>`
})});

S({id:'G2',g:'G',name:'设置 · 外观',state:'已完成',trigger:'设置 → 外观',spec:'CONSTITUTION 模型与系统 / ARCHITECTURE feature:settings',
render:()=>P({
  tab:0,
  appbar:U.bar({back:true,title:'外观'}),
  body:`<div class="scroll">
    ${U.track({title:'浅色',sub:'',icon:'sun',right:''})}
    ${U.track({title:'深色',sub:'',icon:'moon',right:''})}
    ${U.track({title:'随系统',sub:'',icon:'gear',right:U.chip('当前','g')})}
  </div>`
})});

S({id:'G3',g:'G',name:'设置 · 我的音频',state:'已完成',trigger:'设置 → 我的音频',spec:'CONSTITUTION 模型与系统 / ARCHITECTURE feature:settings',
render:()=>P({
  tab:0,
  appbar:U.bar({back:true,title:'我的音频',trailing:['全选','导出']}),
  body:`<div class="scroll">
    ${U.R(U.T('已选 2 项','s')+`<span class="grow"></span>`+U.B('导出','line sm')+U.B('删除','ghost sm'))}
    ${U.track({title:'第一章 声音的起点',sub:'24.8 MB · 12:04',icon:'head',right:U.chip('已选','g')})}
    ${U.track({title:'第二章 语速与停顿',sub:'22.1 MB · 10:38',icon:'head',right:U.chip('已选','g')})}
    ${U.track({title:'第三章 语气与重音',sub:'26.4 MB · 13:12',icon:'head',right:''})}
    ${U.R(U.B('清理中间文件','ghost'))}
  </div>`
})});

S({id:'G4',g:'G',name:'设置 · 开发者工具',state:'已完成',trigger:'设置 → 开发者工具',spec:'CONSTITUTION 模型与系统 / ARCHITECTURE feature:settings',
render:()=>P({
  tab:0,
  appbar:U.bar({back:true,title:'开发者工具'}),
  body:`<div class="scroll">
    ${U.track({title:'版本',sub:'1.0.0',icon:'gear',right:U.chip('正式','line')})}
    ${U.track({title:'构建',sub:'240',icon:'list',right:''})}
    ${U.track({title:'缓存',sub:'318 MB',icon:'book',right:U.B('清理','ghost sm')})}
    ${U.track({title:'日志',sub:'关闭',icon:'tasks',right:U.sw(false)})}
    ${U.R(U.B('展开日志','ghost sm'))}
  </div>`
})});

S({id:'G5',g:'G',name:'模型 · 未就绪',state:'未开始',trigger:'首次进入模型页',spec:'CONSTITUTION 模型与系统 / ARCHITECTURE feature:models',
render:()=>P({
  tab:0,
  appbar:U.bar({back:true,title:'模型'}),
  body:`<div class="scroll">
    ${U.R(U.T('4 份 · 0 已导入','s'))}
    ${U.track({title:'中文女声 · 标准',sub:'412 MB · 未导入',icon:'head',right:U.B('导入','line sm')})}
    ${U.track({title:'中文男声 · 沉稳',sub:'468 MB · 未导入',icon:'head',right:U.B('导入','line sm')})}
    ${U.track({title:'英文女声',sub:'520 MB · 未导入',icon:'head',right:U.B('导入','line sm')})}
    ${U.track({title:'英文男声',sub:'496 MB · 未导入',icon:'head',right:U.B('导入','line sm')})}
  </div>`
})});

S({id:'G6',g:'G',name:'模型 · 已就绪',state:'已完成',trigger:'4 份模型导入完成',spec:'CONSTITUTION 模型与系统 / ARCHITECTURE feature:models',
render:()=>P({
  tab:0,
  appbar:U.bar({back:true,title:'模型'}),
  body:`<div class="scroll">
    ${U.R(U.T('4 份 · 4 已导入','s'))}
    ${U.track({title:'中文女声 · 标准',sub:'412 MB · 已导入',icon:'head',right:U.chip('已导入','g')+U.B('卸载','ghost sm')})}
    ${U.track({title:'中文男声 · 沉稳',sub:'468 MB · 已导入',icon:'head',right:U.chip('已导入','g')+U.B('卸载','ghost sm')})}
    ${U.track({title:'英文女声',sub:'520 MB · 已导入',icon:'head',right:U.chip('已导入','g')+U.B('卸载','ghost sm')})}
    ${U.track({title:'英文男声',sub:'496 MB · 已导入',icon:'head',right:U.chip('已导入','g')+U.B('卸载','ghost sm')})}
  </div>`
})});

S({id:'G7',g:'G',name:'模型 · 导入中',state:'准备中',trigger:'导入一份模型并复制到本地',spec:'CONSTITUTION 模型与系统 / ARCHITECTURE feature:models',
render:()=>P({
  tab:0,
  appbar:U.bar({back:true,title:'模型'}),
  body:`<div class="scroll">
    ${U.R(U.T('1 份复制中 · 2 份排队中','s'))}
    ${U.stat({title:'中文女声 · 标准',pct:62,line:'复制中 · 256 MB / 412 MB',actions:['取消']})}
    ${U.track({title:'中文男声 · 沉稳',sub:'468 MB · 排队中',icon:'head',right:U.chip('排队中','line')})}
    ${U.track({title:'英文女声',sub:'520 MB · 排队中',icon:'head',right:U.chip('排队中','line')})}
    ${U.track({title:'英文男声',sub:'496 MB · 未导入',icon:'head',right:U.B('导入','ghost sm')})}
  </div>`
})});

S({id:'G8',g:'G',name:'模型 · 卸载确认',state:'制作中',trigger:'制作中点卸载 / 点导入',spec:'CONSTITUTION 模型与系统 / ARCHITECTURE feature:models',
render:()=>P({
  tab:0,
  appbar:U.bar({back:true,title:'模型'}),
  body:`<div class="scroll">
    ${U.R(U.T('模型','s')+`<span class="grow"></span>`+U.chip('制作中','w'))}
    ${U.track({title:'中文女声 · 标准',sub:'412 MB · 已导入',icon:'head',right:U.chip('已导入','g')+U.B('卸载','ghost sm')})}
    ${U.track({title:'中文男声 · 沉稳',sub:'468 MB · 未导入',icon:'head',right:U.chip('已暂停','line')})}
    ${U.track({title:'英文女声',sub:'520 MB · 未导入',icon:'head',right:U.chip('已暂停','line')})}
    ${U.dlg({title:'卸载 中文女声 · 标准',text:'删除 412 MB 模型文件，不可撤销。',buttons:['卸载','取消']})}
  </div>`
})});
