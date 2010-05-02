call javase_env
call scala28_env

scala -classpath .;..\swarm.jar;..\selectivecps-library.jar %*
