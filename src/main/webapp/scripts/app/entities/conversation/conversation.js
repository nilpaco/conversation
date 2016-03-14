'use strict';

angular.module('conversationApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('conversation', {
                parent: 'entity',
                url: '/conversations',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'conversationApp.conversation.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/conversation/conversations.html',
                        controller: 'ConversationController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('conversation');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('conversation.detail', {
                parent: 'entity',
                url: '/conversation/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'conversationApp.conversation.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/conversation/conversation-detail.html',
                        controller: 'ConversationDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('conversation');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Conversation', function($stateParams, Conversation) {
                        return Conversation.get({id : $stateParams.id});
                    }]
                }
            })
            .state('conversation.new', {
                parent: 'conversation',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/conversation/conversation-dialog.html',
                        controller: 'ConversationDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    name: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('conversation', null, { reload: true });
                    }, function() {
                        $state.go('conversation');
                    })
                }]
            })
            .state('conversation.edit', {
                parent: 'conversation',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/conversation/conversation-dialog.html',
                        controller: 'ConversationDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Conversation', function(Conversation) {
                                return Conversation.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('conversation', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('conversation.delete', {
                parent: 'conversation',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/conversation/conversation-delete-dialog.html',
                        controller: 'ConversationDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Conversation', function(Conversation) {
                                return Conversation.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('conversation', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
