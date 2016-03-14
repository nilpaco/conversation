'use strict';

angular.module('conversationApp')
    .factory('ConversationSearch', function ($resource) {
        return $resource('api/_search/conversations/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
