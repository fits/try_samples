Redmine::Plugin.register :restrict_changesets do
  name 'Restrict Changesets Plugin'
  author 'fits'
  description ''
  version '0.0.1'
  url ''
  author_url ''

  require 'restricted_changeset'

  Changeset.include RestrictedChangeset
end
