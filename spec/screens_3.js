S({
  id:'E1', g:'E', name:'播放中 · 全屏', state:'正在听',
  trigger:'从章节列表或迷你条进入全屏播放器',
  spec:'CONSTITUTION 播放与收听 / ARCHITECTURE feature:player',
  render: ()=>P({
    appbar: U.bar({back:true, title:'正在听', sub:'山那边', trailing:['倍速']}),
    tabs:false, mini:false,
    body:`<div class="scroll">
      ${U.cover({txt:'山'})}
      ${U.C(`
        ${U.R(U.T('第 2 章 · 全书 2/12','b')+'<span class="grow"></span>'+U.chip('正在听','g'))}
        ${U.R(U.T('第 2 章 山那边','s'))}
        ${U.bar2(63)}
        ${U.R(U.T('12:04','s')+'<span class="grow"></span>'+U.T('19:05','s'))}
      `)}
      ${U.playctl({playing:true})}
      ${U.R(U.T('整本还剩 1:24:06','s')+'<span class="grow"></span>'+U.T('1.0×','s'))}
      ${U.divider()}
      ${U.track({title:'播放队列 · 12 章', sub:'第 2 章', icon:'list', right:U.ic('chev')})}
    </div>`
  })
});

S({
  id:'E2', g:'E', name:'正在听 · 空态', state:'未在播放',
  trigger:'没有收听记录时进入正在听',
  spec:'CONSTITUTION 播放与收听 / ARCHITECTURE feature:player',
  render: ()=>P({
    appbar: U.bar({title:'正在听'}),
    tab:3,
    body:`<div class="scroll">
      ${U.empty('未在播放', ['去我的书'])}
    </div>`
  })
});

S({
  id:'E3', g:'E', name:'播放队列 · 展开', state:'播放队列',
  trigger:'全屏播放器点「播放队列」',
  spec:'CONSTITUTION 播放与收听 / ARCHITECTURE feature:player',
  render: ()=>P({
    appbar: U.bar({back:true, title:'播放队列', sub:'12 章', trailing:['编辑']}),
    tabs:false, mini:false,
    body:`<div class="scroll">
      ${U.R(U.T('接下来 10 章','s')+'<span class="grow"></span>'+U.chip('顺序','line'))}
      ${U.track({title:'第 1 章 起风', sub:'18:42', icon:'head'})}
      ${U.track({title:'第 2 章 山那边', sub:'12:04 / 19:05', icon:'play', cls:'', right:U.chip('正在听','g')})}
      ${U.track({title:'第 3 章 夜路', sub:'16:20', icon:'head'})}
      ${U.track({title:'第 4 章 老屋', sub:'21:08', icon:'head'})}
      ${U.track({title:'第 5 章 河湾', sub:'17:36', icon:'head'})}
      ${U.track({title:'第 6 章 石桥', sub:'19:52', icon:'head'})}
      ${U.track({title:'第 7 章 归途', sub:'20:11', icon:'head'})}
      ${U.track({title:'第 8 章 灯下', sub:'15:44', icon:'head'})}
      ${U.track({title:'第 9 章 长夜', sub:'22:30', icon:'head'})}
      ${U.track({title:'第 10 章 天亮', sub:'14:15', icon:'head'})}
      ${U.track({title:'第 11 章 渡口', sub:'18:03', icon:'head'})}
      ${U.track({title:'第 12 章 山那边', sub:'23:40', icon:'head'})}
      ${U.R(U.B('清空队列','ghost sm')+'<span class="grow"></span>'+U.T('共 2:47:05','s'))}
    </div>`
  })
});

S({
  id:'E4', g:'E', name:'倍速与播放模式', state:'正在听',
  trigger:'播放器点「倍速」',
  spec:'CONSTITUTION 播放与收听 / ARCHITECTURE feature:player',
  render: ()=>P({
    appbar: U.bar({back:true, title:'倍速与模式', sub:'第 2 章 山那边'}),
    tab:3,
    body:`<div class="scroll">
      ${U.C(`${U.R(U.T('倍速','b')+'<span class="grow"></span>'+U.T('1.0×','p b'))}
        ${U.modes(['0.75×','1.0×','1.25×','1.5×','2.0×'], 1)}`)}
      ${U.C(`${U.R(U.T('播放模式','b')+'<span class="grow"></span>'+U.T('顺序','s'))}
        ${U.modes(['顺序','随机','循环'], 0)}`)}
      ${U.C(U.slider('音量', 70, '%'))}
      ${U.R(U.B('完成','pri')+'<span class="grow"></span>'+U.T('1.0× · 顺序','s'))}
    </div>`
  })
});

S({
  id:'E5', g:'E', name:'播放中 · 迷你条', state:'正在听',
  trigger:'播放时离开播放器回到任一空间',
  spec:'CONSTITUTION 播放与收听 / ARCHITECTURE feature:player',
  render: ()=>P({
    appbar: U.bar({title:'正在听', sub:'山那边', trailing:['队列']}),
    tab:3,
    mini: U.mini({title:'山那边 · 第 2 章', sub:'12:04 / 19:05', playing:true}),
    body:`<div class="scroll">
      ${U.C(`
        ${U.R(U.T('第 2 章 山那边','b')+'<span class="grow"></span>'+U.chip('正在听','g'))}
        ${U.bar2(63)}
        ${U.R(U.T('12:04 / 19:05','s')+'<span class="grow"></span>'+U.T('整本还剩 1:24:06','s'))}
      `)}
      ${U.track({title:'第 3 章 夜路', sub:'16:20', icon:'head', right:U.B('播这章','line sm')})}
      ${U.track({title:'第 1 章 起风', sub:'18:42', icon:'head', right:U.B('播这章','line sm')})}
      ${U.track({title:'播放队列 · 12 章', sub:'第 2 章', icon:'list', right:U.ic('chev')})}
    </div>`
  })
});

S({
  id:'E6', g:'E', name:'已停止', state:'已停止',
  trigger:'播放中点「停止」',
  spec:'CONSTITUTION 播放与收听 / ARCHITECTURE feature:player',
  render: ()=>P({
    appbar: U.bar({title:'正在听', sub:'山那边', trailing:['倍速']}),
    tab:3,
    body:`<div class="scroll">
      ${U.C(`
        ${U.R(U.T('第 2 章 山那边','b')+'<span class="grow"></span>'+U.chip('已停止','line'))}
        ${U.bar2(63)}
        ${U.R(U.T('12:04','s')+'<span class="grow"></span>'+U.T('19:05','s'))}
      `)}
      ${U.R(U.B('继续听','pri')+U.B('从头播','line')+U.B('队列','ghost'))}
      ${U.divider()}
      ${U.track({title:'第 3 章 夜路', sub:'16:20', icon:'head'})}
      ${U.track({title:'第 1 章 起风', sub:'18:42', icon:'head'})}
    </div>`
  })
});

S({
  id:'E7', g:'E', name:'接着听', state:'未在播放',
  trigger:'有收听位置时进入正在听',
  spec:'CONSTITUTION 播放与收听 / ARCHITECTURE feature:player',
  render: ()=>P({
    appbar: U.bar({title:'正在听', sub:'接着听'}),
    tab:3,
    body:`<div class="scroll">
      ${U.C(`
        ${U.R(U.cover({txt:'山', small:true})+'<div class="tk-txt">'+U.T('山那边','b')+U.T('第 2 章 山那边','s')+'</div>'+'<span class="grow"></span>'+U.chip('未在播放','line'))}
        ${U.bar2(63)}
        ${U.R(U.T('12:04','s')+'<span class="grow"></span>'+U.T('19:05','s'))}
        ${U.R(U.T('整本还剩 1:24:06','s')+'<span class="grow"></span>'+U.T('上次 2 小时前','s'))}
        ${U.R(U.B('接着听','pri')+'<span class="grow"></span>'+U.B('从头','line sm'))}
      `)}
      ${U.R(U.T('其他书','s')+'<span class="grow"></span>'+U.T('2 本','s'))}
      ${U.track({title:'河岸', sub:'第 5 章 08:12 / 16:40', icon:'book', right:U.B('接着听','line sm')})}
      ${U.track({title:'长夜', sub:'第 1 章 00:00 / 20:30', icon:'book', right:U.B('接着听','line sm')})}
    </div>`
  })
});

S({
  id:'E8', g:'E', name:'章节跳转', state:'正在听',
  trigger:'播放器点章节名或「队列」中的章',
  spec:'CONSTITUTION 播放与收听 / ARCHITECTURE feature:player',
  render: ()=>P({
    appbar: U.bar({back:true, title:'章节跳转', sub:'山那边 · 12 章'}),
    tab:3,
    body:`<div class="scroll">
      ${U.track({title:'第 1 章 起风', sub:'18:42', icon:'head', right:U.B('跳到','ghost sm')})}
      ${U.track({title:'第 2 章 山那边', sub:'12:04 / 19:05', icon:'play', right:U.chip('正在听','g')})}
      ${U.track({title:'第 3 章 夜路', sub:'16:20', icon:'head', right:U.B('跳到','ghost sm')})}
      ${U.track({title:'第 4 章 老屋', sub:'21:08', icon:'head', right:U.B('跳到','ghost sm')})}
      ${U.track({title:'第 5 章 河湾', sub:'17:36', icon:'head', right:U.B('跳到','ghost sm')})}
      ${U.track({title:'第 6 章 石桥', sub:'19:52', icon:'head', right:U.B('跳到','ghost sm')})}
      ${U.track({title:'第 7 章 归途', sub:'20:11', icon:'head', right:U.B('跳到','ghost sm')})}
      ${U.track({title:'第 8 章 灯下', sub:'15:44', icon:'head', right:U.B('跳到','ghost sm')})}
      ${U.track({title:'第 9 章 长夜', sub:'22:30', icon:'head', right:U.B('跳到','ghost sm')})}
      ${U.track({title:'第 10 章 天亮', sub:'14:15', icon:'head', right:U.B('跳到','ghost sm')})}
      ${U.track({title:'第 11 章 渡口', sub:'18:03', icon:'head', right:U.B('跳到','ghost sm')})}
      ${U.track({title:'第 12 章 山那边', sub:'23:40', icon:'head', right:U.B('跳到','ghost sm')})}
    </div>`
  })
});
