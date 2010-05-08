// ==========================================================================
// Project:   TestApp2.tasksController
// Copyright: ©2009 My Company, Inc.
// ==========================================================================
/*globals TestApp2 */

/** @class

  (Document Your Controller Here)

  @extends SC.ArrayController
*/
TestApp2.tasksController = SC.ArrayController.create(
/** @scope TestApp2.tasksController.prototype */ {

  // TODO: Add your own code here.
	addTask: function() {

		var task = TestApp2.store.createRecord(TestApp2.Task, {"title": "New Task"});
		this.selectObject(task);

		return YES;
	}

}) ;
