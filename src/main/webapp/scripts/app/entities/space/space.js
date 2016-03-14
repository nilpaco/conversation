'use strict';

angular.module('conversationApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('space', {
                parent: 'entity',
                url: '/spaces',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'conversationApp.space.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/space/spaces.html',
                        controller: 'SpaceController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('space');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('space.detail', {
                parent: 'entity',
                url: '/space/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'conversationApp.space.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/space/space-detail.html',
                        controller: 'SpaceDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('space');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Space', function($stateParams, Space) {
                        return Space.get({id : $stateParams.id});
                    }]
                }
            })
            .state('space.new', {
                parent: 'space',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/space/space-dialog.html',
                        controller: 'SpaceDialogController',
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
                        $state.go('space', null, { reload: true });
                    }, function() {
                        $state.go('space');
                    })
                }]
            })
            .state('space.edit', {
                parent: 'space',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/space/space-dialog.html',
                        controller: 'SpaceDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Space', function(Space) {
                                return Space.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('space', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('space.delete', {
                parent: 'space',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/space/space-delete-dialog.html',
                        controller: 'SpaceDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Space', function(Space) {
                                return Space.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('space', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
