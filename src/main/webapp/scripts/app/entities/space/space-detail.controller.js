'use strict';

angular.module('conversationApp')
    .controller('SpaceDetailController', function ($scope, $rootScope, $stateParams, entity, Space, Conversation, User) {
        $scope.space = entity;
        $scope.load = function (id) {
            Space.get({id: id}, function(result) {
                $scope.space = result;
            });
        };
        var unsubscribe = $rootScope.$on('conversationApp:spaceUpdate', function(event, result) {
            $scope.space = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
