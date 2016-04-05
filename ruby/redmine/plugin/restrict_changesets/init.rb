Redmine::Plugin.register :restrict_changesets do
  name 'Restrict Changesets Plugin'
  author 'fits'
  description ''
  version '0.0.1'
  url ''
  author_url ''

  require 'custom_issues_show'

  IssuesController.include CustomIssuesShow
end
