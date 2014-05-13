angular.module('codebrag.profile')

    .service('userSettingsService', function($rootScope, $timeout, $q, $http, authService, events) {

        this.load = function() {
            return $http.get('rest/users/settings').then(function(response) {
                return response.data.userSettings;
            });
        };

        this.save = function(settings) {
            return $http.put('rest/users/settings', settings).then(function(response) {
                angular.extend(authService.loggedInUser.settings, response.data.userSettings);
                return response.data.userSettings;
            });
        };

        this.initialize = function() {
            var self = this;
            $rootScope.$on(events.branches.branchChanged, function(e, branchName) {
                self.save({selectedBranch: branchName});
            });
        };

    });