'use strict';

angular.module('conversationApp')
    .controller('ConversationDetailController', function ($scope, $rootScope, $stateParams, entity, Conversation, Message, Space, User) {
        $scope.conversation = entity;
        $scope.load = function (id) {
            Conversation.get({id: id}, function(result) {
                $scope.conversation = result;
            });
        };
        var unsubscribe = $rootScope.$on('conversationApp:conversationUpdate', function(event, result) {
            $scope.conversation = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
