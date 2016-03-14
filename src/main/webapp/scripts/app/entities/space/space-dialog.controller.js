'use strict';

angular.module('conversationApp').controller('SpaceDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Space', 'Conversation', 'User',
        function($scope, $stateParams, $uibModalInstance, entity, Space, Conversation, User) {

        $scope.space = entity;
        $scope.conversations = Conversation.query();
        $scope.users = User.query();
        $scope.load = function(id) {
            Space.get({id : id}, function(result) {
                $scope.space = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('conversationApp:spaceUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.space.id != null) {
                Space.update($scope.space, onSaveSuccess, onSaveError);
            } else {
                Space.save($scope.space, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
