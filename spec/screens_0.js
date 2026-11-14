/* 总工程师样板：2 张，作为子智能体产物的对照基准 + 构建链路冒烟测试。
   它们不是批量画图，是「契约长这样」的可执行示例。 */
S({id:'A9',g:'A',name:'我的书 · 规格基准（总工程师样板）',
  state:'3 本 · 1 本制作中', trigger:'冷启动',
  spec:'CONSTITUTION 内容与导入 / ARCHITECTURE feature:library / STATE_VOCAB §2',
  render:()=>P({tab:0, mini:false, appbar:U.bar({title:'我的书',trailing:['＋ 导入']}),
    body:`<div class="scroll">
      ${U.T('3 本','s')}
      ${U.track({title:'山那边',sub:'12 章 · 已能听 3 章',icon:'book',right:U.stateChip('running')})}
      ${U.track({title:'夜航',sub:'12 章 · 可听',icon:'book',right:U.stateChip('done')})}
      ${U.track({title:'旧巷钟声',sub:'8 章 · 未开始',icon:'book',right:U.stateChip('idle')})}
      ${U.glass(`<div class="row">${U.T('念一段','b')}<span class="grow"></span>${U.B('合成这段','ghost sm')}</div>`)}
    </div>`})});

S({id:'C11',g:'C',name:'正在做 · 规格基准（唯一状态中心）',
  state:'制作中 42%', trigger:'有任务在跑',
  spec:'CONSTITUTION 制作链路 / ARCHITECTURE feature:tasks / STATE_VOCAB §0 §1',
  render:()=>P({tab:2, mini:false, appbar:U.bar({title:'正在做',trailing:['调音台']}),
    body:`<div class="scroll">
      ${U.T('1 个进行中 · 2 个排队','s')}
      ${U.stat({title:'正在做《山那边》',pct:42,line:'第 2 章 · 还剩约 6 分钟',actions:['暂停','看进度 ›']})}
      ${U.T('排队中 2','s')}
      ${U.track({title:'整本《夜航》',sub:'12 章',icon:'tasks',right:U.chip('排队中','line')})}
      ${U.track({title:'选中 3 章 ·《旧巷钟声》',sub:'3 章',icon:'tasks',right:U.chip('排队中','line')})}
      ${U.glass(`<div class="row">${U.T('我的音频 · 3 个成品','b')}<span class="grow"></span>${U.B('查看 ›','ghost sm')}</div>`)}
    </div>`})});
