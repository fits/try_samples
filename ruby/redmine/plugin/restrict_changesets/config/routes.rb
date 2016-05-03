# Plugin's routes
# See: http://guides.rubyonrails.org/routing.html

get 'issues/:id/changesets', :to => 'related_changesets#listup'
