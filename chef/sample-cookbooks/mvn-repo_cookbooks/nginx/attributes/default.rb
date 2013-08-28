#
# Cookbook Name:: nginx
# Attributes:: default
default[:nginx][:dir] = '/etc/nginx'
default[:nginx][:conf_dir] = "#{default[:nginx][:dir]}/conf.d"
default[:nginx][:mvn_repo_dir] = '/var/mvn'
default[:nginx][:server] = 'sample-server'
