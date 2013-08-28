#
# Cookbook Name:: nginx
# Recipe:: default
#

include_recipe 'yum::epel'

package 'nginx'

service 'nginx' do
	supports status: true, restart: true, reload: true
	action [:enable, :start]
end

template 'mvn-repo.conf' do
	path "#{node[:nginx][:conf_dir]}/mvn-repo.conf"
	source 'mvn-repo.conf.erb'
	owner 'root'
	group 'root'
	mode 0644

	notifies :reload, "service[nginx]"
end

directory node[:nginx][:mvn_repo_dir] do
	owner 'vagrant'
	group 'vagrant'
	recursive true
end

