from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import ProfileViewSet, SkillViewSet, SwapRequestViewSet, FeedbackViewSet, CustomAuthToken

router = DefaultRouter()
router.register(r'profiles', ProfileViewSet, basename='profile')
router.register(r'skills', SkillViewSet, basename='skill')
router.register(r'swaps', SwapRequestViewSet, basename='swap')
router.register(r'feedback', FeedbackViewSet, basename='feedback')

urlpatterns = [
    path('', include(router.urls)),
    path('login/', CustomAuthToken.as_view(), name='api-token-auth'),
]
