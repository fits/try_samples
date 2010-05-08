
Ext.onReady(function() {
    var data = [
        ['ToDo - ‹Zp’²¸', 'todo.png'],
        ['ƒƒgƒŠƒNƒX‘ª’èŒ‹‰Ê', 'metrics_results.png']
    ];

    var store = new Ext.data.SimpleStore({
        fields: [
            {name: 'title'},
            {name: 'image'}
        ]
    });

    store.loadData(data);

    var grid = new Ext.grid.GridPanel({
        columns: [
            {header: 'Title', sortable: true, dataIndex: 'title', width: 100},
            {id: 'image', header: 'Image', dataIndex: 'image', 
                renderer: function(value){
                    var tpl = new Ext.Template("<img width='{width}' height='{height}' src='{img}'></img>");
                    return tpl.apply({img: value, width: 300, height: 100});
                }
            }
        ],
        store: store,
        renderTo: 'grid-sample',
        width: 450,
        height: 250,
        stripeRows: true,
        autoExpandColumn: 'image'
    });
});