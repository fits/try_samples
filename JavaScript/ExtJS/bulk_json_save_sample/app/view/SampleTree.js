
Ext.define('Sample.view.SampleTree', {
	extend: 'Ext.tree.Panel',
	alias: 'widget.sampletree',
	store: 'Samples',
	rootVisible: false,
	displayField: 'title',
	split: true,
	tbar: [
		{ xtype: 'button', text: 'update', action: 'update' },
		{ xtype: 'button', text: 'save', action: 'save' }
	],
	viewConfig: {
		plugins: {
			ptype: 'treeviewdragdrop'
		}
	}
});
