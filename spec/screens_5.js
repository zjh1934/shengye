/* H 组 · 系统态（总工程师亲自画：全 App 最干净的样板，不写字只留数字）
   旧版唯一做对的地方，照抄并玻璃化。 */
S({id:'H1',g:'H',name:'通知 · 制作中',
  state:'制作中', trigger:'开始制作后切到后台',
  spec:'STATE_VOCAB §6 系统态样板 / ARCHITECTURE platform:service',
  render:()=>P({tab:0, mini:false, appbar:U.bar({title:'我的书'}),
    body:`<div class="scroll">
      ${U.T('3 本','s')}
      ${U.track({title:'山那边', sub:'12 章 · 已能听 3 章', icon:'book', right:U.stateChip('running')})}
      ${U.track({title:'夜航', sub:'12 章', icon:'book', right:U.stateChip('done')})}
      ${U.track({title:'旧巷钟声', sub:'8 章', icon:'book', right:U.stateChip('idle')})}
    </div>` + U.notif({title:'制作中', text:'', action:'停止', time:'现在'})})});

S({id:'H2',g:'H',name:'通知 · 制作完成',
  state:'已完成', trigger:'任务完成',
  spec:'STATE_VOCAB §6',
  render:()=>P({tab:0, mini:false, appbar:U.bar({title:'我的书'}),
    body:`<div class="scroll">
      ${U.T('3 本','s')}
      ${U.track({title:'山那边', sub:'12 章 · 可听', icon:'book', right:U.stateChip('done')})}
      ${U.track({title:'夜航', sub:'12 章', icon:'book', right:U.stateChip('done')})}
    </div>` + U.notif({title:'制作完成', text:'', action:'去听', time:'现在'})})});

S({id:'H3',g:'H',name:'通知 · 已达时长上限',
  state:'已达时长上限', trigger:'本轮额度用完',
  spec:'STATE_VOCAB §6 / §1 LIMIT_REACHED',
  render:()=>P({tab:2, mini:false, appbar:U.bar({title:'正在做'}),
    body:`<div class="scroll">
      ${U.T('已达时长上限','s')}
      ${U.stat({title:'《山那边》', pct:42, line:'第 2 章 · 24 块', actions:['继续','看进度 ›']})}
    </div>` + U.notif({title:'已达时长上限', text:'', action:'继续', time:'现在'})})});

S({id:'H4',g:'H',name:'悬浮球 · 双环',
  state:'制作中', trigger:'开启悬浮进度球 · 制作中',
  spec:'STATE_VOCAB §6 / OverlayProgress 双球：上=整本 下=本章',
  render:()=>P({tab:1, mini:false, appbar:U.bar({back:true, title:'山那边', sub:'共 12 章'}),
    body:`<div class="scroll">
      ${U.stat({title:'可听', pct:25, line:'3 / 12', actions:['接着听']})}
      ${U.track({title:'1. 起风了', sub:'8:12', icon:'book', right:U.stateChip('done')})}
      ${U.track({title:'2. 山那边', sub:'制作中', icon:'book', right:U.stateChip('running')})}
    </div>` + U.ball({v:42, top:280}) + U.ball({v:68, top:342})})});

S({id:'H5',g:'H',name:'悬浮球 · 单球与权限',
  state:'制作中', trigger:'开启悬浮球 · 缺权限',
  spec:'STATE_VOCAB §6 / CONSTITUTION 权限集合 SYSTEM_ALERT_WINDOW',
  render:()=>P({tab:1, mini:false, appbar:U.bar({back:true, title:'山那边', sub:'共 12 章'}),
    body:`<div class="scroll">
      ${U.C(U.T('悬浮球需要「显示在其他应用上层」','s') + U.R('<span class="grow"></span>' + U.B('去开启','pri sm')))}
      ${U.stat({title:'可听', pct:25, line:'3 / 12', actions:['接着听']})}
      ${U.track({title:'2. 山那边', sub:'制作中', icon:'book', right:U.stateChip('running')})}
    </div>` + U.ball({v:42, top:300})})});
