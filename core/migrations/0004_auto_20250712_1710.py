# Generated by Django 3.2.25 on 2025-07-12 11:40

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('core', '0003_add_timestamp_fields'),
    ]

    operations = [
        migrations.RenameField(
            model_name='swaprequest',
            old_name='sender',
            new_name='requester',
        ),
        migrations.RenameField(
            model_name='swaprequest',
            old_name='receiver',
            new_name='target_user',
        ),
        migrations.RemoveField(
            model_name='swaprequest',
            name='skill_offered',
        ),
        migrations.RemoveField(
            model_name='swaprequest',
            name='skill_requested',
        ),
        migrations.AddField(
            model_name='profile',
            name='offered_skills',
            field=models.TextField(blank=True, null=True),
        ),
        migrations.AddField(
            model_name='profile',
            name='rating',
            field=models.IntegerField(default=0),
        ),
        migrations.AddField(
            model_name='profile',
            name='wanted_skills',
            field=models.TextField(blank=True, null=True),
        ),
        migrations.AddField(
            model_name='swaprequest',
            name='message',
            field=models.TextField(default="I'd like to swap skills with you!"),
        ),
        migrations.AlterField(
            model_name='profile',
            name='availability',
            field=models.CharField(blank=True, max_length=100, null=True),
        ),
        migrations.AlterField(
            model_name='profile',
            name='location',
            field=models.CharField(blank=True, max_length=100, null=True),
        ),
        migrations.AlterField(
            model_name='swaprequest',
            name='status',
            field=models.CharField(choices=[('pending', 'Pending'), ('accepted', 'Accepted'), ('rejected', 'Rejected'), ('completed', 'Completed')], default='pending', max_length=10),
        ),
    ]
