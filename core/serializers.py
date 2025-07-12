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
    skills_count = serializers.SerializerMethodField()
    swaps_count = serializers.SerializerMethodField()

    class Meta:
        model = Profile
        fields = ['id', 'user', 'name', 'location', 'is_public', 'availability', 'skills_count', 'swaps_count']

    def get_skills_count(self, obj):
        return obj.user.skill_set.count()

    def get_swaps_count(self, obj):
        return SwapRequest.objects.filter(sender=obj.user).count() + SwapRequest.objects.filter(receiver=obj.user).count()

class SkillSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)
    user_name = serializers.CharField(source='user.username', read_only=True)

    class Meta:
        model = Skill
        fields = ['id', 'name', 'user', 'user_name', 'is_offered', 'created_at']

class SwapRequestSerializer(serializers.ModelSerializer):
    sender = UserSerializer(read_only=True)
    receiver = UserSerializer(read_only=True)
    skill_requested = SkillSerializer(read_only=True)
    skill_offered = SkillSerializer(read_only=True)
    sender_name = serializers.CharField(source='sender.username', read_only=True)
    receiver_name = serializers.CharField(source='receiver.username', read_only=True)

    class Meta:
        model = SwapRequest
        fields = ['id', 'sender', 'sender_name', 'receiver', 'receiver_name', 
                 'skill_requested', 'skill_offered', 'status', 'created_at']

class SwapRequestCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = SwapRequest
        fields = ['receiver', 'skill_requested', 'skill_offered']

    def validate(self, data):
        # Ensure sender and receiver are different
        if data['receiver'] == self.context['request'].user:
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
