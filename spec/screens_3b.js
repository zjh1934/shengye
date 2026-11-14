S({
  id:'F1', g:'F', name:'调音台 · 展开', state:'可听', trigger:'点调音台入口',
  spec:'CONSTITUTION 调音台（保留） / ARCHITECTURE feature:mixer',
  render: ()=>P({
    appbar: U.bar({back:true, title:'调音台', sub:'小王子', trailing:['重置']}),
    body: U.dim(
      U.glass(
        U.R(U.T('音色','b') + '<span class="grow"></span>' + U.chip('未保存','w')) +
        U.voices(['描述音色','角色音色','参考音色'], 0) +
        U.divider() +
        U.voices(['沉稳男声','温和女声','清亮少年','沙哑老者','标准播音','轻柔女声'], 1) +
        U.divider() +
        U.slider('语速', 52, '%') +
        U.slider('音调', 48, '%') +
        U.slider('情绪', 64, '%') +
        U.divider() +
        U.R(U.T('风格','s')) +
        U.modes(['默认','叙事','古风','电台'], 1) +
        U.divider() +
        U.track({title:'参考音频', sub:'未选择', icon:'volume', right:U.B('导入','line sm')}) +
        U.R(U.B('试听','line') + '<span class="grow"></span>' + U.B('保存设置','pri'))
      )
    ),
    tabs:false, mini:false
  })
});

S({
  id:'F2', g:'F', name:'调音台 · 角色音色', state:'可听', trigger:'切换音色分类',
  spec:'CONSTITUTION 调音台（保留） / ARCHITECTURE feature:mixer',
  render: ()=>P({
    appbar: U.bar({back:true, title:'调音台', sub:'小王子', trailing:['重置']}),
    body: U.dim(
      U.glass(
        U.R(U.T('音色','b') + '<span class="grow"></span>' + U.chip('未保存','w')) +
        U.voices(['描述音色','角色音色','参考音色'], 1) +
        U.divider() +
        U.voices(['旁白','少年','青年','老者','少女','商人'], 0) +
        U.divider() +
        U.slider('语速', 38, '%') +
        U.slider('音调', 62, '%') +
        U.slider('情绪', 78, '%') +
        U.divider() +
        U.R(U.T('风格','s')) +
        U.modes(['默认','叙事','古风','电台'], 2) +
        U.divider() +
        U.R(U.T('旁白','s') + '<span class="grow"></span>' + U.T('11 章','s')) +
        U.R(U.T('角色','s') + '<span class="grow"></span>' + U.T('3 个','s')) +
        U.divider() +
        U.R(U.B('试听','line') + '<span class="grow"></span>' + U.B('保存设置','pri'))
      )
    ),
    tabs:false, mini:false
  })
});

S({
  id:'F3', g:'F', name:'调音台 · 试听对比', state:'可听', trigger:'点试听',
  spec:'CONSTITUTION 调音台（保留） / ARCHITECTURE feature:mixer',
  render: ()=>P({
    appbar: U.bar({back:true, title:'试听', sub:'小王子 · 第一章'}),
    body: U.dim(
      U.glass(
        U.R(U.T('对比','b') + '<span class="grow"></span>' + U.chip('未保存','w')) +
        U.divider() +
        U.R(
          U.C(
            U.T('试听 · 已存配置','b') +
            U.kv('音色','沉稳男声') + U.kv('语速','52%') + U.kv('风格','叙事') +
            U.R(U.B('试听','line sm'))
          , 'grow') +
          U.C(
            U.T('试听 · 当前改动','b') +
            U.kv('音色','沙哑老者') + U.kv('语速','38%') + U.kv('风格','古风') +
            U.R(U.B('试听','sm'))
          , 'grow')
        ) +
        U.divider() +
        U.R(U.B('保存设置','line') + '<span class="grow"></span>' + U.B('完成','ghost'))
      )
    ),
    tabs:false, mini:false
  })
});

S({
  id:'F4', g:'F', name:'调音台 · 未保存离开', state:'可听', trigger:'有改动时返回',
  spec:'CONSTITUTION 调音台（保留） / ARCHITECTURE feature:mixer',
  render: ()=>P({
    appbar: U.bar({back:true, title:'调音台', sub:'小王子'}),
    body: U.glass(
      U.R(U.T('当前音色','b') + '<span class="grow"></span>' + U.chip('未保存','w')) +
      U.divider() +
      U.kv('音色','沙哑老者') + U.kv('语速','38%') + U.kv('音调','62%') + U.kv('风格','古风')
    ) + U.dlg({
      title:'改动未保存',
      text:'音色 · 语速 · 风格',
      buttons:['保存并离开','继续编辑','丢弃并离开']
    }),
    tabs:false, mini:false
  })
});

S({
  id:'F5', g:'F', name:'调音台 · 已保存', state:'可听', trigger:'点保存设置',
  spec:'CONSTITUTION 调音台（保留） / ARCHITECTURE feature:mixer',
  render: ()=>P({
    appbar: U.bar({back:true, title:'调音台', sub:'小王子', trailing:['重置']}),
    body: U.dim(
      U.glass(
        U.R(U.T('音色','b') + '<span class="grow"></span>' + U.chip('已保存','g')) +
        U.voices(['描述音色','角色音色','参考音色'], 0) +
        U.divider() +
        U.voices(['沉稳男声','温和女声','清亮少年','沙哑老者','标准播音','轻柔女声'], 3) +
        U.divider() +
        U.slider('语速', 38, '%') +
        U.slider('音调', 62, '%') +
        U.slider('情绪', 70, '%') +
        U.divider() +
        U.modes(['默认','叙事','古风','电台'], 2) +
        U.divider() +
        U.kv('需重做','3 章') + U.kv('可听','8 章') +
        U.R(U.B('试听','line') + '<span class="grow"></span>' + U.B('完成','ghost'))
      )
    ) + U.toast('已保存'),
    tabs:false, mini:false
  })
});

S({
  id:'F6', g:'F', name:'调音台 · 参考音频', state:'可听', trigger:'点导入参考音频',
  spec:'CONSTITUTION 调音台（保留） / ARCHITECTURE feature:mixer',
  render: ()=>P({
    appbar: U.bar({back:true, title:'调音台', sub:'小王子'}),
    body: U.glass(
      U.R(U.T('参考音色','b') + '<span class="grow"></span>' + U.chip('未保存','w')) +
      U.divider() +
      U.voices(['描述音色','角色音色','参考音色'], 2) +
      U.divider() +
      U.kv('音色','—') + U.kv('语速','52%')
    ) + U.dlg({
      title:'参考音频',
      body: U.track({title:'sample_voice.wav', sub:'0:14 · 44.1kHz', icon:'volume', right:U.chip('已载入','g')}) +
        U.kv('试听进度','0:06 / 0:14') +
        U.bar2(42) +
        U.playctl({playing:true}),
      buttons:['导入','重选','取消']
    }),
    tabs:false, mini:false
  })
});
