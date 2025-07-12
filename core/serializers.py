from rest_framework import serializers
from django.contrib.auth.models import User
from .models import Profile, Skill, SwapRequest, Feedback

class UserRegistrationSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True)
    email = serializers.EmailField(required=False)
    first_name = serializers.CharField(required=False)
    last_name = serializers.CharField(required=False)

    class Meta:
        model = User
        fields = ['username', 'password', 'email', 'first_name', 'last_name']

    def create(self, validated_data):
        user = User.objects.create_user(
            username=validated_data['username'],
            password=validated_data['password'],
            email=validated_data.get('email', ''),
            first_name=validated_data.get('first_name', ''),
            last_name=validated_data.get('last_name', '')
        )
        return user
    
class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['id', 'username', 'email', 'first_name', 'last_name']

class ProfileSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)

    class Meta:
        model = Profile
        fields = ['id', 'user', 'name', 'location', 'is_public', 'availability', 
                 'offered_skills', 'wanted_skills', 'rating']

class SkillSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)
    user_name = serializers.CharField(source='user.username', read_only=True)

    class Meta:
        model = Skill
        fields = ['id', 'name', 'user', 'user_name', 'is_offered', 'created_at']

class SwapRequestSerializer(serializers.ModelSerializer):
    requester = UserSerializer(read_only=True)
    target_user = UserSerializer(read_only=True)
    requester_name = serializers.CharField(source='requester.username', read_only=True)
    target_user_name = serializers.CharField(source='target_user.username', read_only=True)

    class Meta:
        model = SwapRequest
        fields = ['id', 'requester', 'requester_name', 'target_user', 'target_user_name', 
                 'message', 'status', 'created_at']

class SwapRequestCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = SwapRequest
        fields = ['target_user', 'message']

    def validate(self, data):
        # Ensure requester and target_user are different
        if data['target_user'] == self.context['request'].user:
            raise serializers.ValidationError("Cannot send swap request to yourself")
        return data

class FeedbackSerializer(serializers.ModelSerializer):
    from_user = UserSerializer(read_only=True)
    swap = SwapRequestSerializer(read_only=True)
    from_user_name = serializers.CharField(source='from_user.username', read_only=True)

    class Meta:
        model = Feedback
        fields = ['id', 'swap', 'from_user', 'from_user_name', 'comments', 'rating', 'created_at']

class FeedbackCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Feedback
        fields = ['swap', 'comments', 'rating']

    def validate_rating(self, value):
        if value < 1 or value > 5:
            raise serializers.ValidationError("Rating must be between 1 and 5")
        return value

class PublicProfileSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)
    skills = SkillSerializer(many=True, read_only=True)

    class Meta:
        model = Profile
        fields = ['id', 'user', 'name', 'location', 'availability', 'skills']
