from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import (
    ProfileViewSet, SkillViewSet, SwapRequestViewSet, FeedbackViewSet, 
    CustomAuthToken, RegisterView, UserViewSet
)

router = DefaultRouter()
router.register(r'profiles', ProfileViewSet, basename='profile')
router.register(r'skills', SkillViewSet, basename='skill')
router.register(r'swaps', SwapRequestViewSet, basename='swap')
router.register(r'feedback', FeedbackViewSet, basename='feedback')
router.register(r'users', UserViewSet, basename='user')

urlpatterns = [
    path('', include(router.urls)),
    path('auth/login/', CustomAuthToken.as_view(), name='api-token-auth'),
    path('auth/register/', RegisterView.as_view(), name='api-register'),
]
