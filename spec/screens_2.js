/* C 组 · 正在做（任务页 · 全 App 唯一状态中心） */

S({
  id:'C1', g:'C', name:'空闲 · 无任务',
  state:'未开始', trigger:'没有任何任务',
  spec:'STATE_VOCAB §1 / ARCHITECTURE feature:tasks',
  render: ()=>P({
    body: U.empty('暂无任务', ['去我的书']),
    tab:2
  })
});

S({
  id:'C2', g:'C', name:'单个任务 · 制作中',
  state:'制作中', trigger:'一个任务在逐块合成',
  spec:'STATE_VOCAB §1 RUNNING / U.stat 唯一状态来源',
  render: ()=>P({
    body: U.stat({title:'制作中 · 山那边', pct:42, line:'第 2 章 · 还剩约 6 分钟', actions:['暂停','看进度']}),
    tab:2
  })
});

S({
  id:'C3', g:'C', name:'1 个进行中 · 2 个排队',
  state:'排队中', trigger:'提交多个任务',
  spec:'STATE_VOCAB §1 QUEUED / stateChip queued',
  render: ()=>P({
    body: U.stat({title:'制作中 · 山那边', pct:42, line:'第 2 章 · 还剩约 6 分钟', actions:['暂停','看进度']})
      + U.R(U.T('排队 2','s'))
      + U.track({title:'夜航', sub:'12 章', right:U.stateChip('queued')})
      + U.track({title:'旧巷钟声', sub:'3 章', right:U.stateChip('queued')}),
    tab:2
  })
});

S({
  id:'C4', g:'C', name:'已暂停',
  state:'已暂停', trigger:'用户点了暂停',
  spec:'STATE_VOCAB §1 PAUSED',
  render: ()=>P({
    body: U.stat({title:'已暂停 · 山那边', pct:42, line:'第 2 章', actions:['继续','取消']}),
    tab:2
  })
});

S({
  id:'C5', g:'C', name:'失败 · 重试 / 看原因',
  state:'失败', trigger:'某块无法自动恢复',
  spec:'STATE_VOCAB §1 FAILED / §3 REDO',
  render: ()=>P({
    body: U.stat({title:'失败 · 山那边', pct:42, line:'第 3 章 · 需重做 2 块', actions:['重试','看原因']}),
    tab:2
  })
});

S({
  id:'C6', g:'C', name:'已达时长上限',
  state:'已达时长上限', trigger:'本轮额度用完',
  spec:'STATE_VOCAB §1 LIMIT_REACHED',
  render: ()=>P({
    body: U.stat({title:'已达时长上限 · 山那边', pct:68, line:'第 4 章', actions:['继续','看进度']}),
    tab:2
  })
});

S({
  id:'C7', g:'C', name:'已停止',
  state:'已停止', trigger:'用户主动停止',
  spec:'STATE_VOCAB §1 STOPPED',
  render: ()=>P({
    body: U.stat({title:'已停止 · 山那边', pct:35, line:'第 2 章', actions:['继续','删除']}),
    tab:2
  })
});

S({
  id:'C8', g:'C', name:'已完成 · 去听 / 查看成品',
  state:'已完成', trigger:'全部块生成并登记',
  spec:'STATE_VOCAB §1 DONE',
  render: ()=>P({
    body: U.stat({title:'已完成 · 山那边', pct:100, line:'12 章', actions:['去听','查看成品']}),
    tab:2
  })
});

S({
  id:'C9', g:'C', name:'看进度 · 块网格',
  state:'制作中', trigger:'点开看进度',
  spec:'STATE_VOCAB §3 块状态与子阶段 / U.segGrid + U.legend 唯一一屏',
  render: ()=>P({
    appbar: U.bar({back:true, title:'看进度', sub:'山那边'}),
    body: U.stat({title:'制作中 · 山那边', pct:42, line:'第 2 章 · 还剩约 6 分钟', actions:['暂停']})
      + U.R(U.T('正在拼接','s'))
      + U.glass(U.T('第 2 章 · 16 块','s')
        + U.segGrid([
          {c:'done',t:'1'},{c:'done',t:'2'},{c:'done',t:'3'},{c:'done',t:'4'},
          {c:'done',t:'5'},{c:'done',t:'6'},{c:'run',t:'7'},{c:'wait',t:'8'},
          {c:'done',t:'9'},{c:'done',t:'10'},{c:'redo',t:'11'},{c:'wait',t:'12'},
          {c:'wait',t:'13'},{c:'wait',t:'14'},{c:'wait',t:'15'},{c:'wait',t:'16'}
        ])
        + U.legend()),
    tab:2
  })
});

S({
  id:'C10', g:'C', name:'我的音频 · 独立入口',
  state:'可听', trigger:'成品与任务分开',
  spec:'ARCHITECTURE 状态唯一来源 / CONSTITUTION 职责分开',
  render: ()=>P({
    body: U.stat({title:'制作中 · 山那边', pct:42, line:'第 2 章 · 还剩约 6 分钟', actions:['暂停','看进度']})
      + U.divider()
      + U.glass(U.R(U.T('我的音频 · 8 个成品','b') + U.B('查看','pri sm'))),
    tab:2
  })
});

/* D 组 · 续跑与开做（按 STATE_VOCAB §4 的原因分卡） */

S({
  id:'D1', g:'D', name:'续跑 · 意外中断',
  state:'上次意外中断', trigger:'上次进程意外中断后启动',
  spec:'STATE_VOCAB §4 INTERRUPTED',
  render: ()=>P({
    body: U.dlg({
      title:'上次意外中断',
      text:'已做好的 18 块都留着',
      body: U.kv('山那边','第 2 章') + U.kv('已生成','18 块') + U.kv('剩余','6 章'),
      buttons:['接着做','取消']
    }),
    tab:2
  })
});

S({
  id:'D2', g:'D', name:'续跑 · 主动停止',
  state:'上次是你停的', trigger:'上次用户主动停止后启动',
  spec:'STATE_VOCAB §4 USER_STOPPED',
  render: ()=>P({
    body: U.dlg({
      title:'上次是你停的',
      text:'已做好的 12 块都留着',
      body: U.track({title:'山那边', sub:'第 3 章', right:U.stateChip('paused')})
        + U.kv('已生成','12 块'),
      buttons:['继续制作','取消']
    }),
    tab:2
  })
});

S({
  id:'D3', g:'D', name:'续跑 · 模型没齐',
  state:'模型没齐', trigger:'模型份数不足',
  spec:'STATE_VOCAB §4 MODELS_MISSING',
  render: ()=>P({
    body: U.dlg({
      title:'模型没齐',
      text:'还差 2 份',
      body: U.R(U.T('2 / 4 份','big p')) + U.kv('已导入','2 份') + U.kv('还差','2 份'),
      buttons:['去导入']
    }),
    tab:2
  })
});

S({
  id:'D4', g:'D', name:'续跑 · 时长上限',
  state:'上次到时长上限', trigger:'上次到达时长上限后启动',
  spec:'STATE_VOCAB §4 LIMIT_REACHED',
  render: ()=>P({
    body: U.dlg({
      title:'上次到时长上限',
      text:'已做好的 24 块都留着',
      body: U.kv('山那边','第 4 章') + U.kv('已生成','24 块') + U.kv('本轮','24 / 24 块'),
      buttons:['继续制作','取消']
    }),
    tab:2
  })
});

S({
  id:'D5', g:'D', name:'续跑 · 从未启动成功',
  state:'上次没启动成功', trigger:'上次没启动成功',
  spec:'STATE_VOCAB §4 NEVER_STARTED',
  render: ()=>P({
    body: U.dlg({
      title:'上次没启动成功',
      text:'已重排到队列',
      body: U.track({title:'山那边', sub:'12 章', right:U.stateChip('queued')})
        + U.kv('队列','第 1 位'),
      buttons:['去任务页']
    }),
    tab:2
  })
});

S({
  id:'D6', g:'D', name:'续跑 · 队列已暂停',
  state:'队列已暂停', trigger:'队列被暂停',
  spec:'STATE_VOCAB §4 QUEUE_PAUSED',
  render: ()=>P({
    body: U.dlg({
      title:'队列已暂停',
      text:'2 个任务在等',
      body: U.track({title:'夜航', sub:'12 章', right:U.stateChip('queued')})
        + U.track({title:'旧巷钟声', sub:'3 章', right:U.stateChip('queued')}),
      buttons:['继续队列','取消']
    }),
    tab:2
  })
});

S({
  id:'D7', g:'D', name:'自动续跑 · 常驻恢复条',
  state:'自动续跑', trigger:'AUTO 自动接着上次继续',
  spec:'STATE_VOCAB §4 AUTO / 常驻可撤销，非弹窗非 Toast',
  render: ()=>P({
    body: U.glass(U.R(U.T('已接着上次继续','b') + U.B('撤销','ghost sm')))
      + U.stat({title:'制作中 · 山那边', pct:42, line:'第 2 章 · 还剩约 6 分钟', actions:['暂停','看进度']}),
    tab:2
  })
});

S({
  id:'D8', g:'D', name:'开做之前 · 确认卡',
  state:'准备中', trigger:'选好章节后点制作',
  spec:'CONSTITUTION 撤销 PRODUCE 整页 / 改为一张确认卡',
  render: ()=>P({
    appbar: U.bar({back:true, title:'山那边'}),
    body: U.dlg({
      title:'制作确认',
      body: U.kv('已选','第 1–3 章') + U.kv('语速','1.0×') + U.kv('格式','64k · M4A')
        + U.divider() + U.kv('预计','24 块 · 36 分钟')
        + U.R('<span class="grow"></span>' + U.B('更多参数 ›','ghost sm')),
      buttons:['开始制作']
    }),
    tab:1
  })
});
