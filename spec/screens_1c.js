/* B9 / B10：总工程师补缺（B 组后台任务只交到 B8，且写入独立文件避免被其重写覆盖） */
S({id:'B9',g:'B',name:'这一本 · 首帧骨架',
  state:'加载中', trigger:'冷启动首帧',
  spec:'ACCEPTANCE C1 冷启动不卡 / ARCHITECTURE feature:book',
  render:()=>P({tab:1, mini:false, appbar:U.bar({title:'小王子',sub:'共 6 章'}),
    body:`<div class="scroll">
      ${U.skel(2)}
      ${U.skel(4)}
    </div>`})});

S({id:'B10',g:'B',name:'这一本 · 勾章多选',
  state:'未开始', trigger:'点「制作这一本」后勾章',
  spec:'CONSTITUTION 勾章 / ARCHITECTURE feature:book / STATE_VOCAB §2',
  render:()=>P({tab:1, mini:false, appbar:U.bar({back:true, title:'选章制作', trailing:['全选']}),
    body:`<div class="scroll">
      ${U.T('已选 3 章 · 预计 36 分钟','s')}
      ${U.track({title:'1. 起风了', sub:'8:12', icon:'check', right:U.chip('已选','line')})}
      ${U.track({title:'2. 沙漠里的狐狸', sub:'9:40', icon:'check', right:U.chip('已选','line')})}
      ${U.track({title:'3. 玫瑰', sub:'7:28', icon:'check', right:U.chip('已选','line')})}
      ${U.track({title:'4. 蛇', sub:'6:55', icon:'book', right:U.chip('未选','line')})}
      ${U.glass(`<div class="row">${U.T('3 章 · 24 块','s')}<span class="grow"></span>${U.B('开始制作','pri sm')}</div>`)}
    </div>`})});
