'use strict';

angular.module('conversationApp')
    .factory('Message', function ($resource, DateUtils) {
        return $resource('api/messages/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.time = DateUtils.convertLocaleDateFromServer(data.time);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.time = DateUtils.convertLocaleDateToServer(data.time);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.time = DateUtils.convertLocaleDateToServer(data.time);
                    return angular.toJson(data);
                }
            }
        });
    });
