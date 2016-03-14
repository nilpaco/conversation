'use strict';

angular.module('conversationApp')
    .controller('MessageDetailController', function ($scope, $rootScope, $stateParams, entity, Message, Conversation, User) {
        $scope.message = entity;
        $scope.load = function (id) {
            Message.get({id: id}, function(result) {
                $scope.message = result;
            });
        };
        var unsubscribe = $rootScope.$on('conversationApp:messageUpdate', function(event, result) {
            $scope.message = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
