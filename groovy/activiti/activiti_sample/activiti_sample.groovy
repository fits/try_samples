@GrabResolver('https://maven.alfresco.com/nexus/content/groups/public/')
@Grab('org.activiti:activiti-engine:5.14')
@Grab('com.h2database:h2:1.3.174')
import org.activiti.engine.*

// インメモリの H2 DB を使用する設定で ProcessEngine を構築
def engine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration().buildProcessEngine()

def repositoryService = engine.repositoryService

// BPMN ファイルのデプロイ
def dep = repositoryService.createDeployment().addClasspathResource(args[0]).deploy()

println "id: ${dep.id}, name: ${dep.name}, category: ${dep.category}"

// プロセス定義の取得
def pd = repositoryService.createProcessDefinitionQuery().deploymentId(dep.id).singleResult()

// デプロイの ID とプロセス定義の ID が異なる点に注意
println "id: ${pd.id}, key: ${pd.key}, deploymentId: ${pd.deploymentId}"

// プロセス開始 （BPMN に設定された id を key として使用可能）
def pinst = engine.runtimeService.startProcessInstanceByKey('sample', [
	'checker': 'user1',
	'executor': 'user2'
])
/* 下記でも可
def pinst = engine.runtimeService.startProcessInstanceById(pd.id, [
	'checker': 'user1',
	'executor': 'user2'
])
*/

println "key: ${pinst.businessKey}, pid: ${pinst.processDefinitionId}, suspended: ${pinst.suspended}"

def taskService = engine.taskService

// 確認タスクの処理
def task1 = taskService.createTaskQuery().taskAssignee('user1').active().singleResult()

if (task1 == null) {
	println "not found task1"
	return
}

taskService.complete(task1.id, ["checkRes": "OK"])

def task1State = engine.historyService.createHistoricTaskInstanceQuery().processInstanceId(task1.processInstanceId).finished().singleResult()

// 確認タスクの状況確認
println "task[${task1State.id}] start: ${task1State.startTime.format('yyyy/MM/dd HH:mm:ss')}, end: ${task1State.endTime.format('yyyy/MM/dd HH:mm:ss')}"

// 処理タスクの処理
def task2 = taskService.createTaskQuery().taskAssignee('user2').active().singleResult()

if (task2 == null) {
	println "not found task2"
	return
}

taskService.complete(task2.id)

def pdState = engine.historyService.createHistoricProcessInstanceQuery().finished().singleResult()

println "process[${pdState.id}] start: ${pdState.startTime.format('yyyy/MM/dd HH:mm:ss')}, end: ${pdState.endTime.format('yyyy/MM/dd HH:mm:ss')}"
