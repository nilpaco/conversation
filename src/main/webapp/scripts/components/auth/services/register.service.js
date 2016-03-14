'use strict';

angular.module('conversationApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


