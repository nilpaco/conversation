'use strict';

describe('Controller Tests', function() {

    describe('Space Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockSpace, MockConversation, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockSpace = jasmine.createSpy('MockSpace');
            MockConversation = jasmine.createSpy('MockConversation');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Space': MockSpace,
                'Conversation': MockConversation,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("SpaceDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'conversationApp:spaceUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
