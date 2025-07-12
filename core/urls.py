from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import (
    ProfileViewSet, SkillViewSet, SwapRequestViewSet, FeedbackViewSet, 
    CustomAuthToken, RegisterView, UserViewSet, DashboardView
)

router = DefaultRouter()
router.register(r'profiles', ProfileViewSet, basename='profile')
router.register(r'skills', SkillViewSet, basename='skill')
router.register(r'swap-requests', SwapRequestViewSet, basename='swap-request')
router.register(r'feedback', FeedbackViewSet, basename='feedback')
router.register(r'users', UserViewSet, basename='user')

urlpatterns = [
    path('', include(router.urls)),
    path('auth/login/', CustomAuthToken.as_view(), name='api-token-auth'),
    path('auth/register/', RegisterView.as_view(), name='api-register'),
    path('dashboard/', DashboardView.as_view(), name='dashboard'),
    path('profile/update/', ProfileViewSet.as_view({'put': 'update_profile'}), name='profile-update'),
]
