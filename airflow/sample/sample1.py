
from airflow import DAG
from airflow.operators.bash_operator import BashOperator
from datetime import datetime, timedelta

default_args = {
    'start_date': datetime(2019, 7, 21),
    'email_on_failure': False,
    'email_on_retry': False
}

dag = DAG('sample1', catchup = False, default_args = default_args, schedule_interval = timedelta(minutes = 30))

t1 = BashOperator(task_id = 'sample1_task1', bash_command = 'date', dag = dag)

t2 = BashOperator(task_id = 'sample1_task2', bash_command = 'echo run_id={{ run_id }}', dag = dag)

t1 >> t2
