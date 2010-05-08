
Ext.onReady(function() {
    var data = [
        ['なお、実際は SproutCore の sc-build コマンドで生成した HTML や JavaScript を Jersey のリソースクラス等と共に WAR ファイル化してデプロイするような流れになると思うが、今回はそこまではやらず、連携方法を確認するまでに止めている。', 'todo.png', 'test'],
        ['メトリクス測定結果', 'metrics_results.png', 'aaa']
    ];

    var store = new Ext.data.SimpleStore({
        fields: [
            {name: 'title'},
            {name: 'image'},
            {name: 'note'}
        ]
    });

    store.loadData(data);

    var grid = new Ext.grid.EditorGridPanel({
        columns: [
            {id: 'title', header: 'Title', sortable: true, dataIndex: 'title', width: 100,
            	editor: new Ext.grid.GridEditor(new Ext.form.TextArea(), {autoSize: true})},
            {id: 'image', header: 'Image', dataIndex: 'image', 
                renderer: function(value){
                    var tpl = new Ext.Template("<img width='{width}' height='{height}' src='{img}'></img>");
                    return tpl.apply({img: value, width: 300, height: 30});
                }
            },
            {id: 'note', header: 'Note', sortable: true, dataIndex: 'note', width: 100,
            	editor: new Ext.grid.GridEditor(new Ext.form.TextArea(), {autoSize: true})}
        ],
        store: store,
        renderTo: 'grid-sample',
        width: 450,
        height: 250,
        stripeRows: true,
        autoExpandColumn: 'image'
    });
});