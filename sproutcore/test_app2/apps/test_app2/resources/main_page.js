// ==========================================================================
// Project:   TestApp2 - mainPage
// Copyright: ©2009 My Company, Inc.
// ==========================================================================
/*globals TestApp2 */

// This page describes the main user interface for your application.  
TestApp2.mainPage = SC.Page.design({

  // The main pane is made visible on screen as soon as your app is loaded.
  // Add childViews to this pane for views to display immediately on page 
  // load.
  mainPane: SC.MainPane.design({
    childViews: 'topView listView'.w(),

    topView: SC.ToolbarView.design({
		layout: {top: 0, left: 0, right: 0, height: 36},
		anchorLocation: SC.ANCHOR_TOP,
		childViews: 'labelView addButton'.w(),

	    labelView: SC.LabelView.design({
			layout: { centerX: 0, centerY: 0, width: 200, height: 24 },
			textAlign: SC.ALIGN_CENTER,
			tagName: "h1", 
			value: "ToDo Sample"
	    }),
	    addButton: SC.ButtonView.design({
			layout: {centerY: 0, width: 100, height: 24, right: 60},
			title: "Add Task",
			target: "TestApp2.tasksController",
			action: "addTask"
		})
	}),

    listView: SC.ScrollView.design({
		layout: {top: 36, bottom: 10, left:0, right: 0},
		backgroundColor: 'white',
		contentView: SC.ListView.design({
			contentBinding: 'TestApp2.tasksController.arrangedObjects',
			selectionBinding: 'TestApp2.tasksController.selection',
			contentValueKey: 'title',
			canEditContent: YES
		})
	})
    
  })

});
