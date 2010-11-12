<?php
class TasksController extends AppController {
	var $name = "Tasks";

	function index() {
		$tasks = $this->Task->find('all');
		$this->set('tasks', $tasks);
	}

	function add() {
		$this->Task->save($this->data);
		$this->redirect('.');
	}
}
