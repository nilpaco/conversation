 'use strict';

angular.module('conversationApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-conversationApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-conversationApp-params')});
                }
                return response;
            }
        };
    });
