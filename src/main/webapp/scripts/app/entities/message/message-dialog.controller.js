'use strict';

angular.module('conversationApp').controller('MessageDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Message', 'Conversation', 'User',
        function($scope, $stateParams, $uibModalInstance, $q, entity, Message, Conversation, User) {

        $scope.message = entity;
        $scope.messagepreviouss = Message.query({filter: 'messageprevious2-is-null'});
        $scope.messages = Message.query();
        $scope.conversations = Conversation.query();
        $scope.users = User.query();
        $scope.load = function(id) {
            Message.get({id : id}, function(result) {
                $scope.message = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('conversationApp:messageUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.message.id != null) {
                Message.update($scope.message, onSaveSuccess, onSaveError);
            } else {
                Message.save($scope.message, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForTime = {};

        $scope.datePickerForTime.status = {
            opened: false
        };

        $scope.datePickerForTimeOpen = function($event) {
            $scope.datePickerForTime.status.opened = true;
        };
}]);
